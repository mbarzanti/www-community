/*
Noncompliant Code Example (Interdependent Subtasks)
This noncompliant code example is vulnerable to thread-starvation deadlock. It consists of the ValidationService class, which performs various input validation tasks such as checking whether a user-supplied field exists in a back-end database.

The fieldAggregator() method accepts a variable number of String arguments and creates a task corresponding to each argument to enable concurrent processing. The task performs input validation using the ValidateInput class.

In turn, the ValidateInput class attempts to sanitize the input by creating a subtask for each request using the SanitizeInput class. All tasks are executed in the same thread pool. The fieldAggregator() method blocks until all the tasks have finished executing and, when all results are available, returns the aggregated results as a StringBuilder object to the caller.


Assume, for example, that the pool size is set to 6. The ValidationService.fieldAggregator() method is invoked to validate six arguments; consequently, it submits six tasks to the thread pool. Each task submits a corresponding subtask to sanitize the input. The SanitizeInput subtasks must execute before the original six tasks can return their results. However, this is impossible because all six threads in the thread pool are blocked. Furthermore, the shutdown() method cannot shut down the thread pool when it contains active tasks.

Thread-starvation deadlock can also occur when a single-threaded Executor is used, for example, when the caller creates several subtasks and waits for the results.
*/

public final class ValidationService {
  private final ExecutorService pool;

  public ValidationService(int poolSize) {
    pool = Executors.newFixedThreadPool(poolSize);
  }

  public void shutdown() {
    pool.shutdown();
  }

  public StringBuilder fieldAggregator(String... inputs)
      throws InterruptedException, ExecutionException {

    StringBuilder sb = new StringBuilder();
    // Stores the results
    Future<String>[] results = new Future[inputs.length]; 

    // Submits the tasks to thread pool
    for (int i = 0; i < inputs.length; i++) { 
      results[i] = pool.submit(
        new ValidateInput<String>(inputs[i], pool));
    }

    for (int i = 0; i < inputs.length; i++) { // Aggregates the results
      sb.append(results[i].get());
    }
    return sb;
  }
}

public final class ValidateInput<V> implements Callable<V> {
  private final V input;
  private final ExecutorService pool;

  ValidateInput(V input, ExecutorService pool) {
    this.input = input;
    this.pool = pool;
  }

  @Override public V call() throws Exception {
    // If validation fails, throw an exception here
    // Subtask
    Future<V> future = pool.submit(new SanitizeInput<V>(input)); 
    return (V) future.get();
  }
}

public final class SanitizeInput<V> implements Callable<V> {
  private final V input;

  SanitizeInput(V input) {
    this.input = input;
  }

  @Override public V call() throws Exception {
    // Sanitize input and return
    return (V) input;
  }
}

/*
Noncompliant Code Example (Subtasks)

This noncompliant code example contains a series of subtasks that execute in a shared thread pool [Gafter 2006]. The BrowserManager class calls perUser(), which starts tasks that invoke perProfile(). The perProfile() method starts tasks that invoke perTab(), and in turn, perTab starts tasks that invoke doSomething(). BrowserManager then waits for the tasks to finish. The threads are allowed to invoke doSomething() in any order, provided that count correctly records the number of methods executed.

Unfortunately, this program is susceptible to a thread-starvation deadlock. For example, if each of the five perUser tasks spawns five perProfile tasks, where each perProfile task spawns a perTab task, the thread pool will be exhausted, and perTab() will be unable to allocate any additional threads to invoke the doSomething() method.
*/

public final class BrowserManager {
  private final ExecutorService pool = Executors.newFixedThreadPool(10);
  private final int numberOfTimes;
  private static AtomicInteger count = new AtomicInteger(); // count = 0

  public BrowserManager(int n) {
    numberOfTimes = n;
  }

  public void perUser() {
    methodInvoker(numberOfTimes, "perProfile");
    pool.shutdown();
  }

  public void perProfile() {
    methodInvoker(numberOfTimes, "perTab");
  }

  public void perTab() {
    methodInvoker(numberOfTimes, "doSomething");
  }

  public void doSomething() {
    System.out.println(count.getAndIncrement());
  }

  public void methodInvoker(int n, final String method) {
    final BrowserManager manager = this;
    Callable<Object> callable = new Callable<Object>() {
      @Override public Object call() throws Exception {
        Method meth = manager.getClass().getMethod(method);
        return meth.invoke(manager);
      }
    };

    Collection<Callable<Object>> collection = 
        Collections.nCopies(n, callable);
    try {
      Collection<Future<Object>> futures = pool.invokeAll(collection);
    } catch (InterruptedException e) {
      // Forward to handler
      Thread.currentThread().interrupt(); // Reset interrupted status
    }
    // ...
  }

  public static void main(String[] args) {
    BrowserManager manager = new BrowserManager(5);
    manager.perUser();
  }
}
