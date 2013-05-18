package org.colin.Hanoi;

import android.view.MotionEvent;
import android.view.View;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

public class HanoiView extends View {
  private Paint   bg_paint_;
  private Paint   fg_paint_;
  private Paint   tower_paint_;
  private Paint   block_paint_[];
  private Canvas  canvas_;
  private int     w_;
  private int     h_;
  private Hanoi   hanoi_;
  private Bitmap  bitmaps_[];
  private boolean use_bitmap_;
  private Button  newGameButton_;
  private Button  easyButton_;
  private Button  mediumButton_;
  private Button  hardButton_;
  
  public HanoiView(HanoiActivity hanoi) {
    super(hanoi);

    hanoi_ = new Hanoi(this);
    
    bg_paint_ = new Paint();
    fg_paint_ = new Paint();
    
    bg_paint_.setColor(0xFF1A801A);
    bg_paint_.setStyle(Paint.Style.FILL);
 
    fg_paint_.setColor(0xFF000000);
    fg_paint_.setStyle(Paint.Style.FILL);
    
    tower_paint_ = new Paint();
    
    tower_paint_.setColor(0xFF8B4513);
    tower_paint_.setStyle(Paint.Style.FILL);
    
    block_paint_ = new Paint [Hanoi.MAX_BLOCKS];
    
    for (int i = 0; i < Hanoi.MAX_BLOCKS; ++i) {
      block_paint_[i] = new Paint();
    
      int r = 128 + 10*i;
      int g = r;
      int b = r;
      
      block_paint_[i].setColor(0xFF000000 | (r << 16) | (g << 8) | b);
      block_paint_[i].setStyle(Paint.Style.FILL);
    }
    
    bitmaps_ = new Bitmap [Hanoi.MAX_BLOCKS];
    
    for (int i = 0; i < Hanoi.MAX_BLOCKS; ++i)
      bitmaps_[i] = null;
    
    Resources res = getResources();
      
    bitmaps_[0] = BitmapFactory.decodeResource(res, R.drawable.tile1);
    bitmaps_[1] = BitmapFactory.decodeResource(res, R.drawable.tile2);
    bitmaps_[2] = BitmapFactory.decodeResource(res, R.drawable.tile3);
    bitmaps_[3] = BitmapFactory.decodeResource(res, R.drawable.tile4);
    bitmaps_[4] = BitmapFactory.decodeResource(res, R.drawable.tile5);
    bitmaps_[5] = BitmapFactory.decodeResource(res, R.drawable.tile6);
    bitmaps_[6] = BitmapFactory.decodeResource(res, R.drawable.tile7);
    
    use_bitmap_ = false;
    
    newGameButton_ = new Button("New Game");
    easyButton_    = new Button("Easy");
    mediumButton_  = new Button("Medium");
    hardButton_    = new Button("Hard");
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    newGameButton_.move(1000, 680);
    easyButton_   .move( 100, 680);
    mediumButton_ .move( 180, 680);
    hardButton_   .move( 300, 680);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    w_ = canvas.getWidth ();
    h_ = canvas.getHeight();
    
    canvas.drawRect(0, 0, w_, h_, bg_paint_);
    
    canvas_ = canvas;
    
    hanoi_.draw();
    
    newGameButton_.draw(canvas);
    easyButton_   .draw(canvas);
    mediumButton_ .draw(canvas);
    hardButton_   .draw(canvas);
  }

  public void drawBlock(double x1, double y1, double x2, double y2, int ind, int position) {
    Point p1 = windowToPixel(x1, y1);
    Point p2 = windowToPixel(x2, y2);
    
    if (use_bitmap_) {
      Bitmap b = bitmaps_[ind];
    
      canvas_.drawBitmap(b, (p1.x + p2.x)/2 - b.getWidth()/2, (p1.y + p2.y)/2 - b.getHeight()/2, null);
    }
    else {
      canvas_.drawRect(p1.x, p2.y, p2.x, p1.y, block_paint_[ind]);
    }
  }
  
  public void drawLine(double x1, double y1, double x2, double y2) {
    Point p1 = windowToPixel(x1, y1);
    Point p2 = windowToPixel(x2, y2);
    
    canvas_.drawRect(p1.x - 3, p1.y - 3, p2.x + 3, p2.y + 3, tower_paint_);
  }
 
  public void drawText(double x, double y, String str) {
    Point p = windowToPixel(x, y);
    
    canvas_.drawText(str, p.x, p.y - 4, fg_paint_);
  }
  
  public boolean onTouchEvent(MotionEvent event) {
    if      (event.getAction() == MotionEvent.ACTION_DOWN) {
      int mouseX = (int) event.getX();
      int mouseY = (int) event.getY();
      
      if (newGameButton_.contains(mouseX, mouseY)) {
        newGameButton_.setPressed(true);
        invalidate();
        return true;
      }
      if (easyButton_.contains(mouseX, mouseY)) {
        easyButton_.setPressed(true);
        invalidate();
        return true;
      }
      if (mediumButton_.contains(mouseX, mouseY)) {
        mediumButton_.setPressed(true);
        invalidate();
        return true;
      }
      if (hardButton_.contains(mouseX, mouseY)) {
        hardButton_.setPressed(true);
        invalidate();
        return true;
      }

      PointF p = pixelToWindow(mouseX, mouseY);
      
      hanoi_.buttonPress(p.x, p.y);
      
      invalidate();
    }
    else if (event.getAction() == MotionEvent.ACTION_MOVE) {
      int mouseX = (int) event.getX();
      int mouseY = (int) event.getY();
      
      PointF p = pixelToWindow(mouseX, mouseY);
      
      hanoi_.buttonMotion(p.x, p.y);

      invalidate();
    }
    else if (event.getAction() == MotionEvent.ACTION_UP) {
      int mouseX = (int) event.getX();
      int mouseY = (int) event.getY();

      newGameButton_.setPressed(false);
      easyButton_   .setPressed(false);
      mediumButton_ .setPressed(false);
      hardButton_   .setPressed(false);

      if (newGameButton_.contains(mouseX, mouseY)) {
        hanoi_.newGame();
        invalidate();
        return true;
      }
      if (easyButton_.contains(mouseX, mouseY)) {
        hanoi_.setTowerHeight(3);
        hanoi_.newGame();
        invalidate();
        return true;
      }
      if (mediumButton_.contains(mouseX, mouseY)) {
        hanoi_.setTowerHeight(5);
        hanoi_.newGame();
        invalidate();
        return true;
      }
      if (hardButton_.contains(mouseX, mouseY)) {
        hanoi_.setTowerHeight(7);
        hanoi_.newGame();
        invalidate();
        return true;
      }
      
      PointF p = pixelToWindow(mouseX, mouseY);
      
      hanoi_.buttonRelease(p.x, p.y);
      
      invalidate();
    }

    return true;
  }
  
  public Point windowToPixel(double wx, double wy) {
    return new Point((int) (w_*(wx/100.0)), (int) (h_*((100.0 - wy)/100.0)));
  }
  
  public PointF pixelToWindow(int px, int py) {
    return new PointF(100.0f*((1.0f*px)/w_), 100.0f*((1.0f*py)/h_));
  }
 }
