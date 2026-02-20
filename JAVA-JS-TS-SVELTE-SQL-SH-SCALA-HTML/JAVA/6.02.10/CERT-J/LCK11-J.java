/*
Noncompliant Code Example (Intrinsic Lock)
This noncompliant code example uses a thread-safe Book class that cannot be refactored. Refactoring might be impossible, for example, when the source code is unavailable for review or when the class is part of a general library that cannot be extended.

*/

final class Book {
  // Could change its locking policy in the future
  // to use private final locks
  private final String title;
  private Calendar dateIssued;
  private Calendar dateDue;

  Book(String title) {
    this.title = title;
  }

  public synchronized void issue(int days) {
    dateIssued = Calendar.getInstance();
    dateDue = Calendar.getInstance();
    dateDue.add(dateIssued.DATE, days);
  }

  public synchronized Calendar getDueDate() {
    return dateDue;
  }
}
/*
This class fails to commit to its locking strategy (that is, it reserves the right to change its locking strategy without notice). Furthermore, it fails to document that callers can safely use client-side locking. The BookWrapper client class uses client-side locking in the renew() method by synchronizing on a Book instance.


If the Book class were to change its synchronization policy in the future, the BookWrapper class's locking strategy might silently break. For instance, the BookWrapper class's locking strategy would break if Book were modified to use a private final lock object, as recommended by LCK00-J. Use private final lock objects to synchronize classes that may interact with untrusted code. This is because threads that call BookWrapper.getDueDate() would perform operations on the thread-safe Book using its new locking policy. However, threads that call the renew() method would always synchronize on the intrinsic lock of the Book instance. Consequently, the implementation would use two different locks.
*/

// Client
public class BookWrapper {
  private final Book book;

  BookWrapper(Book book) {
    this.book = book;
  }

  public void issue(int days) {
    book.issue(days);
  }

  public Calendar getDueDate() {
    return book.getDueDate();
  }

  public void renew() {
    synchronized(book) {
      if (book.getDueDate().before(Calendar.getInstance())) {
        throw new IllegalStateException("Book overdue");
      } else {
        book.issue(14); // Issue book for 14 days
      }
    }
  }
}
