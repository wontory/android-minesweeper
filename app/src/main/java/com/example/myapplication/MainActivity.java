package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  Chronometer timer;
  BlockButton[][] buttons;

  final int MINES = 10;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 시간
    timer = findViewById(R.id.time);
    timer.setFormat("Time: %s");
    timer.start();

    // 남은 지뢰 수
    TextView mines = findViewById(R.id.mines);
    mines.setText(String.format("Mines: %s", MINES));

    // 게임 필드
    TableLayout table = findViewById(R.id.tableLayout);
    TableRow[] tableRow = new TableRow[9];
    buttons = new BlockButton[9][9];

    // 토글 버튼
    ToggleButton toggle = findViewById(R.id.toggleButton);

    // 필드 버튼 배치
    for (int i = 0; i < 9; i++) {
      tableRow[i] = new TableRow(this);

      for (int j = 0; j < 9; j++) {
        buttons[i][j] = new BlockButton(this, j, i);

        // click 이벤트(Toggle Button)
        buttons[i][j].setOnClickListener(view -> {
            if (toggle.isChecked()) {
              ((BlockButton) view).toggleFlag();
              mines.setText(String.format("Mines: %s", MINES - BlockButton.flags));
            } else {
              breakBlock(view);
            }
          });

        tableRow[i].addView(buttons[i][j]);
      }

      table.addView(tableRow[i]);
    }

    // 지뢰 배치
    for (int i = 0; i < MINES; i++) {
      int x, y;

      do {
        x = (int) (Math.random() * 9);
        y = (int) (Math.random() * 9);
      } while (buttons[y][x].mine);

      buttons[y][x].mine = true;

      // 주변 지뢰 수 계산
      for (int near_y = -1; near_y < 2; near_y++) for (
        int near_x = -1;
        near_x < 2;
        near_x++
      ) if (
        0 <= y + near_y && y + near_y < 9 && 0 <= x + near_x && x + near_x < 9
      ) buttons[y + near_y][x + near_x].neighborMines += 1;
    }
  }

  // 주변의 모든 블록 열기 (Recursive)
  private void breakBlock(View view) {
    int x = ((BlockButton)view).x;
    int y = ((BlockButton)view).y;

    boolean isMine = ((BlockButton)view).breakBlock();

    if (!isMine && ((BlockButton)view).neighborMines == 0) {
      for (int near_y = -1; near_y < 2; near_y++) {
        for (int near_x = -1; near_x < 2; near_x++) {
          if (
            0 <= y + near_y &&
            y + near_y < 9 &&
            0 <= x + near_x &&
            x + near_x < 9 &&
            buttons[y + near_y][x + near_x].isClickable() &&
            !buttons[y + near_y][x + near_x].flag
          ) breakBlock(buttons[y + near_y][x + near_x]);
        }
      }
    }

    // 결과 출력 (게임 종료 시)
    if (isMine || BlockButton.blocks == MINES) {
      String result = isMine ? "\uD83D\uDE22 GAME OVER" : "\uD83C\uDF89 WIN";

      timer.stop();

      for (int i = 0; i < 9; i++)
        for (int j = 0; j < 9; j++)
          buttons[i][j].setClickable(false);

      long current = SystemClock.elapsedRealtime() - timer.getBase();
      int time = (int) (current / 1000);
      int hour = time / (60 * 60);
      int min = time % (60 * 60) / 60;
      int sec = time % 60;

      AlertDialog.Builder resultAlert = new AlertDialog.Builder(MainActivity.this);
      resultAlert.setTitle(result);
      resultAlert.setMessage(hour + "시간 " + min + "분 " + sec +  "초");
      resultAlert.setPositiveButton("다시하기", (dialog, which) -> {
        BlockButton.flags = 0;
        BlockButton.blocks = 0;

        Intent intent = getIntent();
        finish();
        startActivity(intent);

        recreate();
      });
      resultAlert.setCancelable(false);
      resultAlert.show().setCanceledOnTouchOutside(false);
    }
  }
}
