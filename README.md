# Connect Four AI
Created AI that can play Connect Four using the alphabeta algorithm and an evaluation function.

### Getting Started
1) Run: ```javac *.java```. This will compile everything.
2) Run any of the following:
    1) ```java Main -p1 AIAlphaBeta -p2 AIAlphabeta``` - AIAlphaBeta vs. AIAlphaBeta
    2) ```java Main -p1 AIMiniMax -p2 AIAlphabeta```   -   AIMiniMax vs. AIAlphaBeta
    3) ```java Main -p1 AIAlphaBeta``` 				   - AIAlphaBeta vs. human
    4) ```java Main -p2 AIAlphabeta``` 				   -       human vs. AIAlphaBeta
    5) ```java Main``` 								   -       human vs. human

### More Details
1) The AI has to make a decision within 500ms.
2) In a perfect game, the first player will win. So if the AI plays against itself, it should win as player 1.

### Important: I only wrote AIMiniMax.java and AIAlphaBeta.java.
