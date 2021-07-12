package com.mindware.ui.util;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String formatDate(Date date, String sourceFormat){

            SimpleDateFormat sm = new SimpleDateFormat(sourceFormat);

            String strDate = date!=null?sm.format(date):"";

        return strDate;
    }

    public static class DateToString implements Converter<String,Date> {


        @SneakyThrows
        @Override
        public Result<Date> convertToModel(String s, ValueContext valueContext) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            String dateInString = s;
            Date date = formatter.parse(dateInString);
            return Result.ok(date);
        }

        @Override
        public String convertToPresentation(Date date, ValueContext valueContext) {
            return formatDate(date,"dd/MM/yyyy");
        }
    }

    public static class IntegerToString implements Converter<String,Integer>{

        @Override
        public Result<Integer> convertToModel(String s, ValueContext valueContext) {
            return Result.ok(Integer.parseInt(s));
        }

        @Override
        public String convertToPresentation(Integer integer, ValueContext valueContext) {
            return integer.toString();
        }
    }
}
