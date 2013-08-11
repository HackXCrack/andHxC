package es.hackxcrack.andHxC;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

/**
 * Description: Maneja las filas de la lista de mensajes.
 *
 */
public class MessageListAdapter extends ArrayAdapter<MessageInfo> {
    private List<MessageInfo> messages;
    private boolean loading = false;
    private int lastPageRendered = 0;
    private int messagesPerPage;
    private Context context;

    public MessageListAdapter(Context context, int textViewResourceId,
                              List<MessageInfo> messages, int messagesPerPage) {
        super(context, textViewResourceId, messages);
        this.messages = messages;
        this.messagesPerPage = messagesPerPage;
        this.context = context;
    }

    public void setLoading(boolean loading){
        this.loading = loading;
    }


    public boolean isLoading(){
        return loading;
    }

    public void setLastPageRendered(int lastPageRendered){
        this.lastPageRendered = lastPageRendered;
    }


    @Override
    public int getCount(){
        return messages.size() + (((loading) || (messages.size() >= (
                                                     (lastPageRendered + 1) * messagesPerPage)))
                                  ? 1
                                  : 0);
    }


    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater layout = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layout.inflate(R.layout.thread_row_layout, null);
        }
        TextView tvAuthor = (TextView) v.findViewById(R.id.message_author);
        TextView tvText = (TextView) v.findViewById(R.id.message_text);

        if (position >= messages.size()){
            if (tvAuthor != null){
                tvAuthor.setText("");
            }
            if (tvText != null){
                if (loading){
                    tvText.setText(context.getString(R.string.loading_messages));
                }
                else{
                    tvText.setText(context.getString(R.string.load_more_messages));
                }
            }

            return v;
        }

        final MessageInfo msg = messages.get(position);
        if (msg != null){
            if (tvAuthor != null){
                String author = StringEscapeUtils.unescapeHtml(msg.getAuthor());
                if (!author.equals("")){
                    tvAuthor.setText(context.getString(R.string.posted_by) + " " + author);
                }
            }

            if (tvText != null){
                tvText.setText(msg.getMessage());
            }
        }
        return v;
    }
}
