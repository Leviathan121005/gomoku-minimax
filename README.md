## 2. Gomoku Game with Minimax AI
<p  align = "center">
  <img alt = "preview" src = "/preview.png" width = "85%">
</p>

**Language:** Java (version 17+)

**Description:**
A traditional Chinese-Japanese board game (Five in a Row) implemented in Java. It features an AI move function based on the minimax algorithm, with alpha-beta pruning and iterative heuristic evaluation for computational efficiency.

**How to run**: 
- Open this repository in a local codespace.
- Run `java --module-path javafx-sdk-21.0.7/lib --add-modules javafx.controls,javafx.fxml -cp bin gomoku_minimax.GomokuGameFX` to open the game.

**Gameplay**:
- Two players take turns placing stones on the intersections of a 15 × 15 board. 
- The first player to align five stones in a row horizontally, vertically, or diagonally wins.

**Controls**:
- Click on an empty grid intersection to place a stone.
- Click the "AI Move" button to have the AI play the optimal move for the current player.

[Demo Video](https://cuhko365-my.sharepoint.com/:v:/g/personal/123040049_link_cuhk_edu_cn/IQDng33VuQnXQqhditr6vhuXAVNY04LVr7CXcZaPiOk5ggk)