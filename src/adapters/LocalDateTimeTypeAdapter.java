package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void write(JsonWriter out, LocalDateTime time) throws IOException{
        if(time == null){
            out.nullValue();
        }else{
            out.value(formatter.format(time));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException{
        try{
            String date = in.nextString();
            return LocalDateTime.parse(date, formatter);
        }catch (Exception e){
            return null;
        }
    }
}
