package com.example.lab21.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LineChartView extends View {

    private final List<Float> values = new ArrayList<>();
    private final int maxPoints = 60;

    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineChartView(Context context) {
        super(context);

        gridPaint.setColor(Color.parseColor("#E1BEE7"));
        gridPaint.setStrokeWidth(2);

        linePaint.setColor(Color.parseColor("#8A2BE2"));
        linePaint.setStrokeWidth(7);
        linePaint.setStyle(Paint.Style.STROKE);

        fillPaint.setColor(Color.parseColor("#22B388FF"));
        fillPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.parseColor("#4A148C"));
        textPaint.setTextSize(28);
        textPaint.setFakeBoldText(true);

        pointPaint.setColor(Color.parseColor("#8A2BE2"));
        pointPaint.setStyle(Paint.Style.FILL);
    }

    public void addValue(float value) {
        if (values.size() >= maxPoints) {
            values.remove(0);
        }

        values.add(value);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int left = 45;
        int right = width - 25;
        int top = 55;
        int bottom = height - 45;

        for (int i = 0; i <= 4; i++) {
            float y = top + i * ((bottom - top) / 4f);
            canvas.drawLine(left, y, right, y, gridPaint);
        }

        if (values.size() < 2) {
            canvas.drawText("En attente des mesures...", left + 10, height / 2f, textPaint);
            return;
        }

        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (float value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        if (Math.abs(max - min) < 0.001f) {
            max = min + 1f;
        }

        Path linePath = new Path();
        Path fillPath = new Path();

        float lastX = left;
        float lastY = bottom;

        for (int i = 0; i < values.size(); i++) {
            float x = left + i * ((right - left) / (float) (maxPoints - 1));

            float normalized = (values.get(i) - min) / (max - min);
            float y = bottom - normalized * (bottom - top);

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.moveTo(x, bottom);
                fillPath.lineTo(x, y);
            } else {
                linePath.lineTo(x, y);
                fillPath.lineTo(x, y);
            }

            lastX = x;
            lastY = y;
        }

        fillPath.lineTo(lastX, bottom);
        fillPath.close();

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);

        canvas.drawCircle(lastX, lastY, 8, pointPaint);

        canvas.drawText(
                "Min: " + String.format("%.2f", min)
                        + "   Max: " + String.format("%.2f", max),
                left,
                35,
                textPaint
        );
    }
}