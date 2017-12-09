public class AIMiniMax extends AIModule {
	private int AIID;
	private int adversaryID;
	private int startDepth = 5;
	private int maxDepth;
	private int tempChosenMove;
	
	// Keeps track of all the possible ways to win.
	private int[][][] allPossibleWins = {		
		// vertical
		{{0,0},{0,1},{0,2},{0,3}}, {{0,1},{0,2},{0,3},{0,4}}, {{0,2},{0,3},{0,4},{0,5}},
		{{1,0},{1,1},{1,2},{1,3}}, {{1,1},{1,2},{1,3},{1,4}}, {{1,2},{1,3},{1,4},{1,5}},
		{{2,0},{2,1},{2,2},{2,3}}, {{2,1},{2,2},{2,3},{2,4}}, {{2,2},{2,3},{2,4},{2,5}},
		{{3,0},{3,1},{3,2},{3,3}}, {{3,1},{3,2},{3,3},{3,4}}, {{3,2},{3,3},{3,4},{3,5}},
		{{4,0},{4,1},{4,2},{4,3}}, {{4,1},{4,2},{4,3},{4,4}}, {{4,2},{4,3},{4,4},{4,5}},
		{{5,0},{5,1},{5,2},{5,3}}, {{5,1},{5,2},{5,3},{5,4}}, {{5,2},{5,3},{5,4},{5,5}},
		{{6,0},{6,1},{6,2},{6,3}}, {{6,1},{6,2},{6,3},{6,4}}, {{6,2},{6,3},{6,4},{6,5}},
		
		// horizontal
		{{0,0},{1,0},{2,0},{3,0}}, {{1,0},{2,0},{3,0},{4,0}}, {{2,0},{3,0},{4,0},{5,0}}, {{3,0},{4,0},{5,0},{6,0}},
		{{0,1},{1,1},{2,1},{3,1}}, {{1,1},{2,1},{3,1},{4,1}}, {{2,1},{3,1},{4,1},{5,1}}, {{3,1},{4,1},{5,1},{6,1}},
		{{0,2},{1,2},{2,2},{3,2}}, {{1,2},{2,2},{3,2},{4,2}}, {{2,2},{3,2},{4,2},{5,2}}, {{3,2},{4,2},{5,2},{6,2}},
		{{0,3},{1,3},{2,3},{3,3}}, {{1,3},{2,3},{3,3},{4,3}}, {{2,3},{3,3},{4,3},{5,3}}, {{3,3},{4,3},{5,3},{6,3}},
		{{0,4},{1,4},{2,4},{3,4}}, {{1,4},{2,4},{3,4},{4,4}}, {{2,4},{3,4},{4,4},{5,4}}, {{3,4},{4,4},{5,4},{6,4}},
		{{0,5},{1,5},{2,5},{3,5}}, {{1,5},{2,5},{3,5},{4,5}}, {{2,5},{3,5},{4,5},{5,5}}, {{3,5},{4,5},{5,5},{6,5}},
		
		// bottom left to top right
		{{0,0},{1,1},{2,2},{3,3}}, {{0,1},{1,2},{2,3},{3,4}}, {{0,2},{1,3},{2,4},{3,5}},
		{{1,0},{2,1},{3,2},{4,3}}, {{1,1},{2,2},{3,3},{4,4}}, {{1,2},{2,3},{3,4},{4,5}},
		{{2,0},{3,1},{4,2},{5,3}}, {{2,1},{3,2},{4,3},{5,4}}, {{2,2},{3,3},{4,4},{5,5}},
		{{3,0},{4,1},{5,2},{6,3}}, {{3,1},{4,2},{5,3},{6,4}}, {{3,2},{4,3},{5,4},{6,5}},

		// top left to bottom right
		{{0,3},{1,2},{2,1},{3,0}}, {{0,4},{1,3},{2,2},{3,1}}, {{0,5},{1,4},{2,3},{3,2}},
		{{1,3},{2,2},{3,1},{4,0}}, {{1,4},{2,3},{3,2},{4,1}}, {{1,5},{2,4},{3,3},{4,2}},
		{{2,3},{3,2},{4,1},{5,0}}, {{2,4},{3,3},{4,2},{5,1}}, {{2,5},{3,4},{4,3},{5,2}},
		{{3,3},{4,2},{5,1},{6,0}}, {{3,4},{4,3},{5,2},{6,1}}, {{3,5},{4,4},{5,3},{6,2}} 
	};

	@Override
	public void getNextMove(final GameStateModule state) {
		// Setting the player ID's.
		this.AIID = state.getActivePlayer();
		this.adversaryID = 1 + this.AIID % 2;

		int value;
		int overallMaxValue = Integer.MIN_VALUE;
		
		chosenMove = -1;
		maxDepth = startDepth;
		tempChosenMove = -1;
		
		// If this move is available, make it, since it increases the chance of winning.
		if(state.getAt(3, 0) == 0){
			chosenMove = 3;
			return;
		}

		// First attempt to get a move.
		value = maxValue(state, maxDepth);
		chosenMove = tempChosenMove;
		overallMaxValue = value;
		
		// If there is still time left, try increasing the depth and getting new minimax results.
		try{
			while(!terminate && state.getCoins() + maxDepth <= 42){
				// Increase the depth by 2, 1 for min and 1 for max.
				maxDepth += 2;
				tempChosenMove = -1;
				value = maxValue(state, maxDepth);
				// If the new move gets a higher value, set the chosenMove to it.
				if(value > overallMaxValue && !terminate){
					overallMaxValue = value;
					chosenMove = tempChosenMove;
				}
			}
		} catch (Exception tempEx){
			return;
		}
	}

	private int maxValue(GameStateModule state, int depth){
		// If chosenMove is already set to something, just throw an exception to return.
		if(terminate && chosenMove > -1){
			throw new RuntimeException("No Time Left!");
		}

		// Return the result of the evaluation function if either the max depth was reached, the end of a game was found,
		// or the terminate flag was set.
		if (terminate || state.isGameOver() || depth <= 0){
			return evaluate(state);
		}

		int max = Integer.MIN_VALUE;
		int maxTempVal;
		
		// Moves in the middle of the board increase the chance of winning since they give more options to the player, so
		// the algorithm will start searching from the middle out.
		int movePriority[] = {3,4,2,5,1,6,0};
		for (int i : movePriority) {
			if (state.canMakeMove(i)) {
				state.makeMove(i);
				maxTempVal = minValue(state, depth - 1);
				state.unMakeMove();
				if (maxTempVal >= max) {
					max = maxTempVal;
					if(depth == maxDepth){
						tempChosenMove = i;
					}
				}
			}
		}
		return max;
	}

	private int minValue(GameStateModule state, int depth) {
		if(terminate && chosenMove > -1){
			throw new RuntimeException("No Time Left!");
		}

		if (terminate || state.isGameOver() || depth <= 0){
			return evaluate(state);
		}
		
		int min = Integer.MAX_VALUE;
		int minTempVal;

		int movePriority[] = {3,4,2,5,1,6,0};
		for (int i : movePriority) {
			if (state.canMakeMove(i)) {
				state.makeMove(i);
				minTempVal = maxValue(state, depth - 1);
				state.unMakeMove();
				if (minTempVal < min) {
					min = minTempVal;
				}
			}
		}
		return min;
	}

	// Evaluation function.
	private int evaluate(GameStateModule state) {
		// Return if the terminate flag is on and a move has already been chosen.
		if(terminate && chosenMove > -1){
			throw new RuntimeException("No Time Left!");
		}

		int value = 0;
		int maxValue;
		int minValue;
		int x;
		int y;

		// If the game is over, set value to 10000000 if max won or -10000000 if max lost.
		// If the game ended in a draw, return 0.
		if (state.isGameOver()) { 
			if (state.getWinner() == this.AIID) 
			  value = 10000000; 
			else if (state.getWinner() == this.adversaryID) 
			  value = -10000000; 
			else 
			  return 0; 
		}

		// Checking every possible win
		for (int[][] possibleWin : this.allPossibleWins) {
			minValue = 0;
			maxValue = 0;
			x = -1;
			y = -1;

			// For each winning line, add the number of coins each player has on that line.
			for (int[] possibleWinPosition : possibleWin) {
				int playerIDAt = state.getAt(possibleWinPosition[0], possibleWinPosition[1]);
				if(playerIDAt == this.AIID) {
					maxValue++;
				} else if(playerIDAt == this.adversaryID) {
					minValue++;
				} else {
					x = possibleWinPosition[0];
					y = possibleWinPosition[1];
				}
				// If both players have coins on that line, break out since it won't be counted.
				if(maxValue > 0 && minValue > 0){break;}
			}

			// Make some adjustments to the values.
			if (!(maxValue > 0 && minValue > 0)) {
				if(minValue == 3) {
					minValue = 10;
					// Checks if the player is able to win the next turn.
					if((y == 0 || (y > 0 && state.getAt(x, y - 1) != 0)) && state.getActivePlayer() == this.adversaryID){
						minValue = 100000;
					}
				}

				// Same as before, but adding points for the max player.
				if(maxValue == 3) {
					maxValue = 10;
					if((y == 0 || (y > 0 && state.getAt(x, y - 1) != 0)) && state.getActivePlayer() == this.AIID){
						maxValue = 100000;
					}
				}
				value += (maxValue - minValue);
			}
		}
		return value;
	}
}
