# Number Guessing Game (Java, Swing GUI)

A Swing GUI game where the computer picks a secret random number and you try to guess it, with higher/lower hints after every guess.

## Features

- Random number generated at the start of each round
- "Too High!" / "Too Low!" / "Correct!" feedback after every guess
- Attempt counter shown throughout the round (remaining + progress bar in GUI)
- Attempt limit per difficulty — running out of attempts ends the round with "You Lost!" and reveals the number
- "Play Again" option after each round ("New Round" button in the GUI)
- Score tracking across rounds: end-of-session summary shows `Round X — guessed in Y attempts` per round
- Best-score persistence: fewest-attempts records per difficulty are saved to `number-guessing-scores.properties` and survive between sessions
- Bonus difficulty levels:

| Difficulty | Range  | Attempts |
|------------|--------|----------|
| Easy       | 1–50   | 10       |
| Medium     | 1–100  | 7        |
| Hard       | 1–200  | 5        |

## Project structure

```
src/
  GameEngine.java               # Shared game logic (random number, attempts, history, best scores)
  ScoreStore.java               # Persists best scores to a properties file
  NumberGuessingGameGUI.java    # Swing GUI front-end (JTextField + buttons)
```

## Build & run

Requires a JDK 17+.

```bat
build.bat                  REM compiles src/ into out/
run-gui.bat                REM launches the Swing GUI game
```

Or manually:

```bat
javac -d out src\*.java
java -cp out NumberGuessingGameGUI
```

## How to play

1. Pick a difficulty (Easy / Medium / Hard).
2. Enter your guess and press Enter or click **Guess**.
3. After each guess you get "Too High!", "Too Low!" or "Correct!".
4. Guess the number before you run out of attempts.
5. Click **New Round** to play again. Your round-by-round summary and best scores are shown at the bottom of the window as you play.




<img width="1920" height="1140" alt="Screenshot 2026-11-15 131634" src="https://github.com/user-attachments/assets/5a85f869-fc13-4570-9faf-0794b3ff321b" />
<img width="1920" height="1140" alt="Screenshot 2026-11-15 131554" src="https://github.com/user-attachments/assets/eb63b384-f67b-4672-9a83-822a7434adb2" />
<img width="1920" height="1140" alt="Screenshot 2026-11-15 131335" src="https://github.com/user-attachments/assets/9e78948d-e70a-4e8b-a56f-28b7b0a08855" />
