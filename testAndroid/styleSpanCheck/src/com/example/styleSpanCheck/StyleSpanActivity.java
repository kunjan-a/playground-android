package com.example.styleSpanCheck;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StyleSpanActivity extends Activity {

    private static final String TAG = "styleSpanChecker";
    private TextView _spanStartTv;
    private EditText _editText;
    private TextView _spanEndTv;
    private TextView _spanTextTv;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_span_check);
        _editText = (EditText) findViewById(R.id.et);
        _spanStartTv = (TextView) findViewById(R.id.tv_span_start);
        _spanEndTv = (TextView) findViewById(R.id.tv_span_end);
        _spanTextTv = (TextView) findViewById(R.id.tv_span_text);

        final Button addStyleSpanBtn = (Button) findViewById(R.id.btn_);

        _editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateMentionSpanTextViews();
            }
        });
        addStyleSpanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable editable = _editText.getText();
                String spanText ="@Kunjan A";
                editable.append(' ');
                final int length = editable.length();
                editable.append(spanText);//.append(' ');
                final int end = length + spanText.length();
                final int start = length;
                Log.d(TAG, "setting span from: "+start+" to:"+end);
                editable.setSpan(new EditTextMentionSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                printMentionSpans();
                updateMentionSpanTextViews();
            }
        });
    }

    private void updateMentionSpanTextViews() {
        final Editable editableText = _editText.getEditableText();
        final EditTextMentionSpan[] spans = editableText.getSpans(0, _editText.length(), EditTextMentionSpan.class);
        printMentionSpans();
        StringBuilder start = new StringBuilder("");
        StringBuilder end = new StringBuilder("");
        StringBuilder text = new StringBuilder("");
        for (EditTextMentionSpan span : spans) {
            final int spanStart = editableText.getSpanStart(span);
            final int spanEnd = editableText.getSpanEnd(span);
            final CharSequence spanText = editableText.subSequence(spanStart, spanEnd>=_editText.length()?_editText.length():spanEnd + 1);
            start.append(' ').append(spanStart).append(',');
            end.append(' ').append(spanEnd).append(',');
            text.append(" '").append(spanText).append("',");
        }
        if(start.length()>0){
            _spanStartTv.setText(start.subSequence(1, start.length() - 1));
            _spanEndTv.setText(end.subSequence(1,end.length()-1));
            _spanTextTv.setText(text.subSequence(1,text.length()-1));
        }
    }

    private void printMentionSpans()
    {
        Log.d(TAG,"Printing mention spans");
        final Editable editableText = _editText.getEditableText();
        final EditTextMentionSpan[] mentionSpans = editableText
                .getSpans(0, editableText.length(),
                        EditTextMentionSpan.class);
        for (EditTextMentionSpan mentionSpan : mentionSpans) {

            final int spanEnd = editableText.getSpanEnd(mentionSpan);
            Log.d(TAG,"mention span "+editableText.getSpanStart(mentionSpan)+" to "+spanEnd);
        }

    }

}
