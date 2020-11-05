package com.miempresa.autores;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

  private EditText inputBook;
  private TextView bookTitle;
  private TextView bookAuthor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    inputBook = (EditText)findViewById(R.id.inputbook);
    bookTitle = (TextView)findViewById(R.id.bookTitle);
    bookAuthor = (TextView)findViewById(R.id.bookAuthor);
  }

  public void searchBook(View view) {
    String searchString = inputBook.getText().toString();
    new GetBook(bookTitle,bookAuthor).execute(searchString);
  }

  public class GetBook extends AsyncTask<String,Void,String>{

    private WeakReference<TextView> mTextTitle;
    private WeakReference<TextView> mTextAuthor;

    public GetBook(TextView mTextTitle, TextView mTextAuthor) {
      this.mTextTitle = new WeakReference<>(mTextTitle);
      this.mTextAuthor = new WeakReference<>(mTextAuthor);
    }

    @Override
    protected String doInBackground(String... strings) {
      return NetUtilities.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s){
      super.onPostExecute(s);
      try{
        JSONObject jsonObject = new JSONObject(s);
        JSONArray itemsArray = jsonObject.getJSONArray("items");
        int i = 0;
        String title = null;
        String author = null;
        while(i < itemsArray.length() && (title == null && author == null)){
          JSONObject book = itemsArray.getJSONObject(i);
          JSONObject volumeInfo = book.getJSONObject("volumeInfo");
          try{
            title = volumeInfo.getString("title");
            author = volumeInfo.getString("authors");
          } catch (JSONException e) {
            e.printStackTrace();
          }
          i++;
        }
        if(title != null && author != null){
          mTextTitle.get().setText(title);
          mTextAuthor.get().setText(author);
        }else{
          mTextTitle.get().setText("No existen resultados para la consulta");
          mTextAuthor.get().setText("");
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

}