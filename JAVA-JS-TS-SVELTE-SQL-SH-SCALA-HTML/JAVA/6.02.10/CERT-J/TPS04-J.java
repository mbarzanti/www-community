/*
Noncompliant Code Example
This noncompliant code example consists of an enumeration of days (Day) and two classes (Diary and DiaryPool). The Diary class uses a ThreadLocal variable to store thread-specific information, such as each task's current day. The initial value of the current day is Monday; it can be changed later by invoking the setDay() method. The class also contains a threadSpecificTask() instance method that performs a thread-specific task.

The DiaryPool class consists of the doSomething1() and doSomething2() methods that each start a thread. The doSomething1() method changes the initial (default) value of the day to Friday and invokes threadSpecificTask(). However, doSomething2() relies on the initial value of the day (Monday) and invokes threadSpecificTask(). The main() method creates one thread using doSomething1() and two more using doSomething2().

The DiaryPool class creates a thread pool that reuses a fixed number of threads operating off a shared, unbounded queue. At any point, no more than numOfThreads threads are actively processing tasks. If additional tasks are submitted when all threads are active, they wait in the queue until a thread is available. The thread-local state of the thread persists when a thread is recycled.
*/

public enum Day {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
}

public final class Diary {
  private static final ThreadLocal<Day> days =
      new ThreadLocal<Day>() {
    // Initialize to Monday
    protected Day initialValue() {
      return Day.MONDAY;
    }
  };

  private static Day currentDay() {
    return days.get();
  }

  public static void setDay(Day newDay) {
    days.set(newDay);
  }

  // Performs some thread-specific task
  public void threadSpecificTask() {
    // Do task ...
  }
}

public final class DiaryPool {
  final int numOfThreads = 2; // Maximum number of threads allowed in pool
  final Executor exec;
  final Diary diary;

  DiaryPool() {
    exec = (Executor) Executors.newFixedThreadPool(numOfThreads);
    diary = new Diary();
  }

  public void doSomething1() {
    exec.execute(new Runnable() {
        @Override public void run() {
          diary.setDay(Day.FRIDAY);
          diary.threadSpecificTask();
        }
    });
  }

  public void doSomething2() {
    exec.execute(new Runnable() {
        @Override public void run() {
          diary.threadSpecificTask();
       }
    });
  }

  public static void main(String[] args) {
    DiaryPool dp = new DiaryPool();
    dp.doSomething1(); // Thread 1, requires current day as Friday
    dp.doSomething2(); // Thread 2, requires current day as Monday
    dp.doSomething2(); // Thread 3, requires current day as Monday
  }
}
