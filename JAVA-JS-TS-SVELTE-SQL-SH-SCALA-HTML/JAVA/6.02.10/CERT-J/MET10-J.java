/*
Noncompliant Code Example (Rock-Paper-Scissors)
This program implements the classic game of rock-paper-scissors, using the compareTo() operator to determine the winner of a game:
However, this game violates the required transitivity property because rock beats scissors, scissors beats paper, but rock does not beat paper.
*/

class GameEntry implements Comparable {
  public enum Roshambo {ROCK, PAPER, SCISSORS}
  private Roshambo value;

  public GameEntry(Roshambo value) {
    this.value = value;
  }

  public int compareTo(Object that) {
    if (!(that instanceof GameEntry)) {
      throw new ClassCastException();
    }
    GameEntry t = (GameEntry) that;
    return (value == t.value) ? 0
      : (value == Roshambo.ROCK && t.value == Roshambo.PAPER) ? -1
      : (value == Roshambo.PAPER && t.value == Roshambo.SCISSORS) ? -1
      : (value == Roshambo.SCISSORS && t.value == Roshambo.ROCK) ? -1
      : 1;
  }
}
