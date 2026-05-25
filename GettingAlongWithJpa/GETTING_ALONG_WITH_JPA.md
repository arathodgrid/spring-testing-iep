# Getting Along with JPA (EntityManager Experiments)

This module originally had no domain model, so for the purpose of the exercise I introduced a simple one-to-many:

- Parent: `Library`
- Child: `Book`

Mapping (intentionally minimal, to surface lifecycle behaviors):

- `Library.books`: `@OneToMany(mappedBy="library", cascade={PERSIST, MERGE})`
- `Book.library`: `@ManyToOne(optional=true)` with `library_id` nullable (so we can test "Child without Parent")
- IDs: `@GeneratedValue(strategy = IDENTITY)` (H2 in-memory for tests)

All experiments live in:

- `org.jpa.gettingalongwithjpa.jpa.lifecycle.*` test classes (split by topic for readability)

Important test constraint: tests are **not** annotated with `@Transactional`. Where JPA requires a transaction (`persist`, `merge`, `flush`) the tests use `TransactionTemplate` explicitly.

## Save Parent without ID

- `repository.save(parentWithoutId)`:
  - ID gets assigned.
  - Under the hood Spring Data chooses `persist` when it considers the entity "new" (default: `id == null`).

- `entityManager.persist(parentWithoutId)`:
  - Becomes managed.
  - Insert happens on flush/commit; with `IDENTITY`, the ID is typically assigned when the insert executes.

- `entityManager.merge(parentWithoutId)`:
  - The passed instance is **not** attached. `merge()` returns a **different** managed instance.
  - After flush, the managed copy has an ID; the original object still has `id == null`.

## Save Parent with an initialized ID

- `repository.save(parentWithId)`:
  - Spring Data treats `id != null` as "not new", so it behaves like `merge()`.
  - In this module (Hibernate 7.2 + `IDENTITY`), attempting to `save()` an entity with a preset id **when no row exists** can fail with a stale/optimistic-lock style exception (it tries to update a row that isn't there).

- `entityManager.persist(parentWithId)`:
  - Fails in our setup (the provider treats it as not-new/detached because it already has an identifier).

- `entityManager.merge(parentWithId)`:
  - Same caveat as `save()`: with a preset id and no existing row, it can fail.

## Insert Parent, then save another Parent with same ID

- `repository.save(anotherWithSameId)`:
  - Updates the existing row (effectively an "upsert"-like experience).

- `entityManager.persist(anotherWithSameId)`:
  - Fails (trying to `persist` a non-new identity).

- `entityManager.merge(anotherWithSameId)`:
  - Updates the existing row.

## Save Parent with Children (children not present in DB)

Because `Library.books` cascades `PERSIST` and `MERGE`:

- `repository.save(parentWithNewChildren)` works (children are inserted via cascade).
- `entityManager.persist(parentWithNewChildren)` works (children are inserted via cascade).
- `entityManager.merge(parentWithNewChildren)` works, but remember the "managed copy" rule for `merge()`.

## Save Parent with Children (children already present in DB)

This is where cascade choice really shows up.

In our tests, the "children already in DB" objects are detached instances with IDs already assigned.

- `repository.save(newParentWithDetachedExistingChildren)` fails
- `entityManager.persist(newParentWithDetachedExistingChildren)` fails

Reasoning: in both of those codepaths, the parent is treated as new, so the provider tries to cascade **PERSIST** into the children, but the children are not new (they already have IDs), so you get a "detached entity passed to persist" style error.

- `entityManager.merge(newParentWithDetachedExistingChildren)` works

Reasoning: `merge()` is designed to reattach/copy state of detached graphs.

## Save Child without Parent

Because `Book.library_id` is nullable in this module:

- `repository.save(childWithoutParent)` works
- `entityManager.persist(childWithoutParent)` works
- `entityManager.merge(childWithoutParent)` works

If `library_id` were non-nullable, this scenario would fail at flush with a constraint violation.

## Save Child with Parent initialized, but Parent not present in DB

We intentionally do **not** cascade from `Book -> Library` (no cascade on `@ManyToOne`), so:

- `repository.save(childWithTransientParent)` fails
- `entityManager.persist(childWithTransientParent)` fails

Also in this module:

- `entityManager.merge(childWithTransientParent)` fails

Reasoning: without cascade from child -> parent, Hibernate refuses to flush a `Book` row that references an unsaved transient `Library`.

## Save Child with Parent present in DB, but detached

If the parent row exists and the parent object has an ID, then the child insert can use that foreign key value:

- `repository.save(childWithDetachedExistingParent)` works
- `entityManager.persist(childWithDetachedExistingParent)` works
- `entityManager.merge(childWithDetachedExistingParent)` works

## Dirty Checking: modify Parent without explicitly saving

Two versions of the same idea:

1. Fetch without an outer transaction (entity becomes detached when the repository method returns), modify, then flush:
   - No update occurs because the modified entity instance is not managed in the current persistence context.

2. Start an outer transaction, fetch, modify, flush:
   - Update occurs via dirty checking at flush/commit time.

In other words: "change tracking" only works for **managed** entities inside an active persistence context.

## Next Discussion Points (mentor)

- How your real domain’s cascade settings (`PERSIST`, `MERGE`, `ALL`, none) change the "children already exist" scenarios.
- How ID generation strategy (`IDENTITY` vs `SEQUENCE`) changes *when* IDs appear and *when* inserts happen.
- How Spring Data’s "isNew" decision interacts with manually assigned IDs, and whether `Persistable.isNew()` is appropriate in your domain.
