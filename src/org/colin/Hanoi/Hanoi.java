package org.colin.Hanoi;

class Hanoi {
  public static final int NUM_TOWERS = 3;

  public static final int START_TOWER = 0;
  public static final int END_TOWER   = 2;
  public static final int MAX_BLOCKS  = 12;

  class Move {
    int from_tower;
    int to_tower;
    
    Move() {
      from_tower = 0;
      to_tower   = 0;
    }
  };

  class Tower {
    boolean blocks[];
    
    Tower() {
      blocks = new boolean [MAX_BLOCKS];
      
      for (int i = 0; i < MAX_BLOCKS; ++i)
        blocks[i] = false;
    }
  };

  public Hanoi(HanoiView view) {
    init();
    
    view_ = view;
  }

  public void setTowerHeight(int h) { towerHeight_ = h; }

  public int getTowerHeight() { return towerHeight_; }

  public void init() {
    tower_ = new Tower [NUM_TOWERS];
    
    for (int i = 0; i < NUM_TOWERS; ++i)
      tower_[i] = new Tower();
    
    current_move = new Move();
 
    moving_ = false;
    
    setTowerHeight(7);
    
    newGame();
  }

  public void newGame() {
    int tower_height = getTowerHeight();

    /* Set up the Blocks so they are all on the start tower */

    for (int t = 0; t < NUM_TOWERS; ++t) {
      Tower tower = tower_[t];

      for (int i = 0; i < tower_height; ++i)
        tower.blocks[i] = false;
    }

    Tower tower = tower_[START_TOWER];

    for (int i = 0; i < tower_height; ++i)
      tower.blocks[i] = true;

    //------

    /* Reset Current Number of Moves */

    number_of_moves = 0;

    //------

    /* Reset the Target Number of Moves */

    target_number_of_moves = 1;

    for (int i = 0; i < tower_height; ++i)
      target_number_of_moves *= 2;

    target_number_of_moves--;

    //------

    /* Set up Drawing Constants */

    total_block_width  = 25;
    total_block_height = 50;

    block_spacing = 1;

    block_width  = total_block_width/tower_height;
    block_height = (total_block_height - block_spacing*(tower_height - 1))/tower_height;

    border_width  = (100 - NUM_TOWERS*total_block_width )/6;
    border_height = (100 -            total_block_height)/2;
  }

  public void draw() {
    //canvas.setDataRange(0, 0, 100, 100);

    int current_position[] = new int [NUM_TOWERS];

    for (int i = 0; i < NUM_TOWERS; ++i)
      current_position[i] = 0;

    //------

    /* Draw Towers */

    for (int i = 0; i < NUM_TOWERS; ++i)
      drawTower(i);

    //------

    /* Draw Blocks */

    int tower_height = getTowerHeight();

    for (int t = 0; t < NUM_TOWERS; ++t) {
      Tower tower = tower_[t];

      for (int i = 0; i < tower_height; ++i) {
        if (! tower.blocks[i]) continue;

        drawBlock(tower_height - i, t, i, current_position[t]++);
      }
    }

    //------

    /* Display Number of Moves */

    String msg = "Moves  = " + number_of_moves + " : Target = " + target_number_of_moves;

    centerText(50.0, 0.0, -0.5, 1.0, msg);

    //------

    /* Display if puzzled solved */

    if (getTopOfTower(0) == -1 && getTopOfTower(1) == -1)
      screenMessage("Congratulations - Puzzle Solved");
  }

  public void buttonPress(double x, double y) {
    moving_ = selectBlock(x, y);
    
    press_x_ = x;
    press_y_ = y;
    
    move_dx_ = 0.0;
    move_dy_ = 0.0;
  }

  public void buttonMotion(double x, double y) {
    move_dx_ = x - press_x_;
    move_dy_ = press_y_ - y;
  }
  
  public void buttonRelease(double x, double y) {
    releaseBlock(x, y);
    
    moving_ = false;
  }

  public void drawBlock(int width, int tower, int ind, int position) {
    //final double offsets[] = { 1, 2, 2.9, 3.7, 4.35, 4.9, 5.4, 6.0, 7, 8, 9, 10 };
    
    double min_x = border_width + tower*(total_block_width + 2*border_width);
    double min_y = border_height;

    int tower_height = getTowerHeight();

    double x_offset = (tower_height - width)*block_width/2;
    double y_offset = position*(block_height + block_spacing);

    double x1 = min_x + x_offset;
    double y1 = min_y + y_offset;
    double x2 = x1 + (width*block_width);
    double y2 = y1 + block_height;

    if (moving_ && current_move.from_tower == tower) {
      int from_top = getTopOfTower(current_move.from_tower);
      
      if (ind == from_top) {
        x1 += move_dx_;
        y1 += move_dy_;
        x2 += move_dx_;
        y2 += move_dy_;
      }
    }
    
    view_.drawBlock(x1, y1, x2, y2, ind, position);
  }

  public void drawTower(int tower) {
    int tower_height = getTowerHeight();

    double min_x = border_width + tower*(total_block_width + 2*border_width);
    double min_y = border_height - 0.5;
    double max_x = min_x + total_block_width;
    double max_y = min_y + tower_height*(block_height + block_spacing);

    view_.drawLine(min_x, min_y, max_x, min_y);
    view_.drawLine((min_x + max_x)/2, min_y, (min_x + max_x)/2, max_y);

    String msg = "Tower " + (tower + 1);

    centerText((min_x + max_x)/2, max_y + 2, -0.5, 1.0, msg);
  }

  public boolean selectBlock(double x, double y) {
    double min_x[] = new double [NUM_TOWERS];
    double max_x[] = new double [NUM_TOWERS];

    for (int i = 0; i < NUM_TOWERS; ++i) {
      min_x[i] = border_width + i*(total_block_width + 2*border_width);
      max_x[i] = min_x[i] + total_block_width;
    }

    current_move.from_tower = -1;

    for (int i = 0; i < NUM_TOWERS; ++i)
      if (x >= min_x[i] && x <= max_x[i])
        current_move.from_tower = i;

    if (current_move.from_tower == -1) {
      if (x <= max_x[0])
        current_move.from_tower = 0;
      else
        current_move.from_tower = 2;
    }
    
    return true;
  }

  public void releaseBlock(double x, double y) {
    double min_x[] = new double [NUM_TOWERS];
    double max_x[] = new double [NUM_TOWERS];

    for (int i = 0; i < NUM_TOWERS; ++i) {
      min_x[i] = border_width + i*(total_block_width + 2*border_width);
      max_x[i] = min_x[i] + total_block_width;
    }

    current_move.to_tower = -1;

    for (int i = 0; i < NUM_TOWERS; ++i)
      if (x >= min_x[i] && x <= max_x[i])
        current_move.to_tower = i;

    if (current_move.to_tower == -1) {
      if (x <= max_x[0])
        current_move.to_tower = 0;
      else
        current_move.to_tower = 2;
    }

    if (validMove()) {
      doMove();
    }
    else {
      //errMsg_ = "Invalid Move - Blocks must always be stacked by size";
    }
  }

  public void solve(int block, int tower) {
    int tower_height = getTowerHeight();

    if (block == tower_height - 1) {
      if (getTowerForBlock(block) != tower) {
        current_move.from_tower = getTowerForBlock(block);
        current_move.to_tower   = tower;

        doMove();
      }
    }
    else {
      if (getTowerForBlock(block) == tower)
        solve(block + 1, tower);
      else
        solve(block + 1, notTower(getTowerForBlock(block), tower));

      current_move.from_tower = getTowerForBlock(block);
      current_move.to_tower   = tower;

      doMove();

      solve(block + 1, tower);
    }
  }

  public int notTower(int tower1, int tower2) {
    return (NUM_TOWERS - tower1 - tower2);
  }

  public int getTowerForBlock(int block) {
    for (int t = 0; t < NUM_TOWERS; ++t) {
      Tower tower = tower_[t];

      if (tower.blocks[block])
        return t;
    }

    return -1;
  }

  public boolean validMove() {
    return validMove(current_move.from_tower, current_move.to_tower);
  }

  public boolean validMove(int from_tower, int to_tower) {
    int from_top = getTopOfTower(from_tower);
    int to_top   = getTopOfTower(to_tower);

    if (from_top > to_top)
      return true;
    else
      return false;
  }

  public int getTopOfTower(int tower_number) {
    int top = -1;

    int tower_height = getTowerHeight();

    Tower tower = tower_[tower_number];

    for (int i = 0; i < tower_height; ++i) {
      if (tower.blocks[i])
        top = i;
    }

    return top;
  }

  public void doMove() {
    doMove(current_move.from_tower, current_move.to_tower);
  }

  public void doMove(int from_tower, int to_tower) {
    int from_top = getTopOfTower(from_tower);

    Tower tower1 = tower_[from_tower];
    Tower tower2 = tower_[to_tower  ];

    tower1.blocks[from_top] = false;
    tower2.blocks[from_top] = true;

    ++number_of_moves;
  }

  public int countBlocksOnTower(int tower_number) {
    int count = 0;

    int tower_height = getTowerHeight();

    Tower tower = tower_[tower_number];

    for (int i = 0; i < tower_height; ++i)
      if (tower.blocks[i])
        ++count;

    return count;
  }

  public boolean solved() {
    int tower_height = getTowerHeight();

    Tower tower = tower_[END_TOWER];

    for (int i = 0; i < tower_height; ++i)
      if (! tower.blocks[i])
        return false;

    return true;
  }

  public boolean isBlock(int tower, int pos) {
    assert(tower < NUM_TOWERS);

    assert(pos < getTowerHeight());

    return tower_[tower].blocks[pos];
  }

  public void screenMessage(String message) {
    centerText(50.0, 100.0, -0.5, -1.0, message);
  }

  public void centerText(double x, double y, double xc, double yc, String text) {
    double text_width;
    double text_ascent;
    double text_descent;

    //canvas.getTextExtents(text, &text_width, &text_ascent, &text_descent);
    text_width   = 10;
    text_ascent  = 12;
    text_descent = 2;

    double xo = xc* text_width ;
    double yo = yc*(text_ascent + text_descent);

    view_.drawText(x + xo, y + yo, text);
  }
  
  HanoiView view_;
  
  Tower   tower_[];
  int     towerHeight_;

  double  total_block_width;
  double  total_block_height;
  double  block_width;
  double  block_height;
  double  border_width;
  double  border_height;
  double  block_spacing;
  Move    current_move;
  int     number_of_moves;
  int     target_number_of_moves;
  boolean moving_;
  double  press_x_;
  double  press_y_;
  double  move_dx_;
  double  move_dy_;
}
