# Dependency Injection — a learning note

> A concept explainer, grounded in this project's own code (the `Property`
> classes from Task 1). Not a spec — a note to learn from. If you already skimmed
> `conventions/kotlin-spring.md` §8, this is the long version of *why*.

## 1. The problem it solves

Most classes need *other* objects to do their job. Your `PropertyController`
needs a `PropertyService`; that service needs a `PropertyRepository`; that
repository needs a database connection. Those "other objects" are its
**dependencies**.

The question is: **who creates them?**

If a class creates its own dependencies, it looks like this:

```kotlin
class PropertyController {
    // the controller builds its entire dependency chain itself
    private val service = PropertyService(PropertyRepositoryImpl(dataSource))
}
```

That's brittle:
- The controller is now welded to *one specific* `PropertyService` and repository.
- To test the controller, you can't slip in a fake service — it hard-codes the real one.
- The controller has to know how to build things that aren't its concern (the repository, the datasource, their settings…).

Multiply that across dozens of classes and you get a tangle where everything
knows how to construct everything.

## 2. The idea: don't create your dependencies — *receive* them

Dependency Injection flips it around: a class **declares what it needs** and lets
something else **create and hand it in**. The class never builds its own
dependencies.

```kotlin
class PropertyController(
    private val service: PropertyService,   // "I need this" — but I don't build it
)
```

That's the whole idea in one line. This is exactly what you wrote in Task 1 — so
you've already been doing DI.

The fancy name for the principle behind it is **Inversion of Control (IoC)**:
the class gives up *control* over how its dependencies are made. Something
outside is *in control* of that now.

## 3. The three flavors (and why one wins)

| Flavor | How | Verdict |
|---|---|---|
| **Constructor injection** | dependencies are constructor parameters | ✅ **preferred** |
| Field injection | `@Autowired` on a `lateinit var` field | ❌ avoid |
| Setter injection | a setter method takes the dependency | rarely needed |

**Constructor injection wins** because:
- The dependencies are **visible** — read the constructor, see exactly what the
  class needs. No hidden `@Autowired` fields buried in the body.
- The object is **valid the moment it exists** — you can't construct it without
  its dependencies, so there's no "half-built" state.
- It's **immutable** — `val`, set once, never reassigned.
- It's **testable** — in a test you just call `PropertyService(fakeRepository)`
  directly, no framework needed.

This is why `conventions/kotlin-spring.md` §8 says "constructor injection only."

## 4. Who does the injecting? The container

Something has to actually *create* all these objects and *wire them together* —
create the repository, pass it to the service, pass the service to the
controller. That something is the **DI container** (a.k.a. **IoC container**).

In Spring, the container is the **ApplicationContext**. It's the invisible engine
that boots when you run the app.

## 5. How Spring does it — using your own code

Spring is itself the DI container, so there's no separate setup. Two ingredients:

**(a) You mark classes as "beans"** — objects Spring should manage — with
stereotype annotations:

```kotlin
@RestController                 // PropertyController is a bean
@Service                        // PropertyService is a bean
// PropertyRepository is a bean automatically (Spring Data)
```

**(b) At startup Spring wires them together:**
1. **Component scan** — Spring scans under your base package
   (`com.boardinghouse.backend`) and finds every `@RestController`, `@Service`,
   etc. *(This is why a class in the wrong package silently isn't found — it's
   outside the scan.)*
2. **Instantiate** — it creates **one** instance of each bean.
3. **Autowire by constructor** — for each bean, it looks at the constructor
   parameters and injects the matching beans.

So your Task 1 graph is built for you, top to bottom:

```
PropertyController(propertyService = ↓)
        └── PropertyService(repository = ↓)
                    └── PropertyRepository  (Spring Data provides the impl)
```

You wrote three constructors; Spring assembled the whole chain. You never once
called `PropertyService(...)` yourself.

> **Note:** with a *single* constructor, Spring injects it automatically — you
> don't even need `@Autowired`. That's why your classes have no `@Autowired`
> anywhere and it still works.

**Beans are singletons by default** — Spring makes *one* `PropertyService` and
shares it everywhere it's needed. It doesn't make a new one per request.

## 6. Why this is worth it

- **Testability** — swap a fake in tests: `PropertyService(FakePropertyRepository())`.
  No database needed to unit-test the service's logic.
- **Loose coupling** — a class depends on *what* it needs, not on *how to build
  it*. You can change how a repository is constructed without touching the
  service.
- **No wiring boilerplate** — you don't write "create A, create B, give B to A"
  glue code. The container does it.

## 7. Contrast with your Flutter work app (asenso)

You *have* met DI before — it just looked different because Flutter isn't a DI
framework, so the work app adds one (GetIt + Injectable):

| | Flutter (asenso) | Spring (this project) |
|---|---|---|
| DI container | GetIt — added as a package | built into Spring (ApplicationContext) |
| Register a class | `@injectable` + generated config | `@Service` / `@RestController` |
| Get a dependency | `getIt<X>()` or constructor | constructor parameter |
| Wiring config | a `di/` module + codegen | none — annotations *are* the config |

Same concept, less ceremony: in Spring the framework *is* the container, so the
annotation on the class is the whole registration.

## 8. Interview-ready summary

Common questions you can now answer:
- *"What is DI?"* — A class receives its dependencies from outside instead of
  creating them itself.
- *"What's IoC?"* — The principle: a class gives up control over building its
  dependencies; a container controls that.
- *"Constructor vs field injection?"* — Constructor: visible, immutable,
  testable, no half-built objects. Field injection hides dependencies — avoid.
- *"What's a Spring bean?"* — An object the Spring container creates and manages.
- *"How does Spring know what to inject?"* — Component scan finds beans;
  constructor parameters are matched to beans by type.

## 9. The one-line takeaway

> **Dependency Injection = "declare what you need in your constructor; let the
> container build and hand it to you."** You already did it in Task 1 — Spring's
> been the container the whole time.
