package com.think.core.executor.reduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 20:27
 * @description :
 */
public class TReducer<Source,Result> {




    private ILocalMapTask<Source> mapper;

    private ILocalReduceTask<Source,Result> reducer;

    private ILocalCombiner<Result> combiner;

    public static void main(String[] args) {

        List<String> list =new ArrayList();
        list.add("x dada");
        list.add("m");
        list.add("ma da as");
        list.stream().flatMap(new Function<String, Stream<?>>() {
            @Override
            public Stream<?> apply(String s) {
                return Arrays.stream(s.split(" "));
            }
        }).forEach(t->{
            System.out.println(t );
        });

        final Stream<String> stringStream = list.stream().flatMap(t ->
                Arrays.stream(t.split(","))
        );


        int reduce = stringStream
                .flatMap(t -> Arrays.stream(t.split(" ")))
                .mapToInt(t -> t.length())
                .reduce(1, (a, b) -> {
                    System.out.println(a +" +  "+ b + " compute  " + (a + b ));
                    return a + b;
                });

    }



}
