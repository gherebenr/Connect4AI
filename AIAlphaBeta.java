public class AIAlphaBeta extends AIModule {
	private int AIID;
	private int adversaryID;
	private int startDepth = 5;
	private int maxDepth;
	private int tempChosenMove;
	
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
		// setting the player ID's
		this.AIID = state.getActivePlayer();
		this.adversaryID = 1 + this.AIID % 2;
		
		int value;
		int overallMaxValue = Integer.MIN_VALUE;
		
		chosenMove = -1;
		maxDepth = startDepth;
		tempChosenMove = -1;
		
		// if this move is available, make it, since it increases the chance of winning
		if(state.getAt(3, 0) == 0){
			chosenMove = 3;
			return;
		}
		
		// almost guaranteed to be able to get the minimax results at this depth
		// depends on speed of CPU. if it doesn't work, change startDepth from 5 to 3
		value = maxValue(state, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		chosenMove = tempChosenMove;
		overallMaxValue = value;
		
		// return if found a winning move
		if(overallMaxValue >= 10000000){
		    return;
	    }
		
		// if there is still time left, try increasing the depth and getting new minimax results.
		try{
			while(!terminate && maxDepth <= 42){
				// increase the depth by 2, 1 for min and 1 for max.
				maxDepth += 2;
				tempChosenMove = -1;
				value = maxValue(state, maxDepth, overallMaxValue, Integer.MAX_VALUE);
				// if the new move gets a higher value, set the chosenMove to it
				if(value > overallMaxValue){
					overallMaxValue = value;
					chosenMove = tempChosenMove;
				}
			}
		} catch (Exception tempEx){
			return;
		}
	}

	private int maxValue(GameStateModule state, int depth, int alpha, int beta){
		// if chosenMove is already set to something, just throw an exception to return.
		if(terminate && chosenMove > -1){
			throw new RuntimeException("No Time Left!");
		}
		
		if (terminate || state.isGameOver() || depth <= 0){
			return utility(state);
		}
		
		int max = Integer.MIN_VALUE;
		int maxTempVal;
		// moves in the middle of the board increase the chance of winning since they give more options to the player
		// so I'm prioritizing the center moves.
		int movePriority[] = {3,4,2,5,1,6,0};
		for (int i : movePriority) {
			if (state.canMakeMove(i)) {
				state.makeMove(i);
				maxTempVal = minValue(state, depth - 1, alpha, beta);
				state.unMakeMove();
				if (maxTempVal >= max) {
					max = maxTempVal;
				}
				if (max >= beta){
					return max;
				}
				if(max > alpha){
					alpha = max;
					if(depth == maxDepth){
						tempChosenMove = i;
					}
				}
			}
		}
		return max;
	}


	private int minValue(GameStateModule state, int depth, int alpha, int beta) {
		if(terminate && chosenMove > -1){
			throw new RuntimeException("No Time Left!");
		}
		if (terminate || state.isGameOver() || depth <= 0){
			return utility(state);
		}
		int min = Integer.MAX_VALUE;
		int minTempVal;
		int movePriority[] = {3,4,2,5,1,6,0};
		for (int i : movePriority) {
			if (state.canMakeMove(i)) {
				state.makeMove(i);
				minTempVal = maxValue(state, depth - 1, alpha, beta);
				state.unMakeMove();
				if (minTempVal < min) {
					min = minTempVal;
				}
				if (min <= alpha){
					return min;
				}
				if(min < beta){
					beta = min;
				}
			}
		}
		return min;
	}

	private int utility(GameStateModule state) {
		if(terminate && chosenMove > -1){
			throw new RuntimeException("No Time Left!");
		}

		// if the game is over, depending on who won, return a really high value, a really low value or 0
		if (state.isGameOver()) {
			if (state.getWinner() == this.AIID)
				return 10000000;
			else if (state.getWinner() == this.adversaryID)
				return -10000000;
			else
				return 0;
		}

		
		int value = 0;
		int maxValue;
		int minValue;
		int x;
		int y;

		// checking every possible win
		for (int[][] possibleWin : this.allPossibleWins) {
			minValue = 0;
			maxValue = 0;
			x = 0;
			y = 0;

			// for each winning line, add the number of coins each player has on that line
			for (int[] possibleWinPosition : possibleWin) {
				// if both players have coins on that line, break out since it won't be counted
				if(maxValue > 0 && minValue > 0){
					break;
				}
				int playerIDAt = state.getAt(possibleWinPosition[0], possibleWinPosition[1]);
				if (playerIDAt == this.AIID) {
					maxValue++;
				} else if (playerIDAt == this.adversaryID) {
					minValue++;
				} else {
					x = possibleWinPosition[0];
					y = possibleWinPosition[1];
				}
			}
			if (!(maxValue > 0 && minValue > 0)) {
				if (maxValue == 3) {
					// this is for when the line is diagonal and there is nothing under 
					// the position that would get the win. need to check if it will be possible 
					// for my AI to win
					if (y > 0 && state.getAt(x, y - 1) == 0) {
						if ((this.AIID == 1 && y % 2 == 0) || (this.AIID == 2 && y % 2 == 1)){
							maxValue += 100;
						}
					} else if (state.getActivePlayer() == this.AIID) { // this is when the AI is one move away from winning
						return 100000;
					} else {
						maxValue += 10; // still count it if the adversary has the chance to block, because it might make a mistake
					}
				}
				// same as before, but adding points for the min player.
				if (minValue == 3) {
					if (y > 0 && state.getAt(x, y - 1) == 0) {
						if ((this.adversaryID == 1 && y % 2 == 0) || (this.adversaryID == 2 && y % 2 == 1)){
							minValue += 100;
						}
					} else if (state.getActivePlayer() == this.adversaryID) {
						return -100000;
					} else {
						minValue += 10;
					}
				}
				value += (maxValue - minValue);
			}
		}
		return value;
	}
}
