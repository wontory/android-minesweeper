package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatButton;

public class BlockButton extends AppCompatButton {
  int x, y;
  boolean mine, flag;
  int neighborMines;
  static int flags = 0, blocks = 0;

  public BlockButton(Context context) {
    super(context);
  }

  public BlockButton(Context context, int x, int y) {
    super(context);
    this.x = x;
    this.y = y;
    this.mine = false;
    this.flag = false;
    this.neighborMines = 0;

    // 레이아웃 파라미터 초기값 설정
    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
      TableRow.LayoutParams.WRAP_CONTENT,
      TableRow.LayoutParams.WRAP_CONTENT,
      1.0f
    );
    setLayoutParams(layoutParams);

    blocks += 1;
  }

  // 깃발 꽂기 & 해제
  public void toggleFlag() {
    if (flag) {
      flags -= 1;
      setText("");
    } else {
      flags += 1;
      setText("\uD83D\uDEA9");
    }

    flag = !flag;
  }

  // 블록 열기
  public boolean breakBlock() {
    if (!flag) {
      setClickable(false);
      setBackgroundColor(Color.TRANSPARENT);

      blocks -= 1;

      if (mine) {
        setText("\uD83D\uDCA3");
        return true;
      } else {
        setColor();
        if (neighborMines != 0) setText(String.valueOf(neighborMines));
        return false;
      }
    }

    return false;
  }

  void setColor() {
    switch (neighborMines) {
      case 1:
        setTextColor(Color.rgb(0, 154, 0));
        break;
      case 2:
        setTextColor(Color.rgb(125, 138, 0));
        break;
      case 3:
        setTextColor(Color.rgb(197, 105, 0));
        break;
      default:
        setTextColor(Color.rgb(255, 0, 0));
        break;
    }
  }
}
