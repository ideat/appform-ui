package com.mindware.ui.util;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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

    public static class DoubleToIntegerConverter implements Converter<Double, Integer> {

        private static final long serialVersionUID = 1L;

        @Override
        public Result<Integer> convertToModel(Double presentation, ValueContext valueContext) {
            return Result.ok(presentation.intValue());
        }

        @Override
        public Double convertToPresentation(Integer model, ValueContext valueContext) {
            return model == null?0.0:model.doubleValue();
        }

    }



    public static String generateRandomPassword(){
        Random random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
