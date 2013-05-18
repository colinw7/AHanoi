package org.colin.Hanoi;

import android.app.Activity;
import android.os.Bundle;

public class HanoiActivity extends Activity {
  private HanoiView view;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    view = new HanoiView(this);

    setContentView(view);
  }
}
