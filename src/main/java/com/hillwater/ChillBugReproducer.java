package com.hillwater;

//import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.twitter.chill.java.ArraysAsListSerializer;

/**
 * Created by zhong on 16/03/04.
 */
public class ChillBugReproducer {
    public static void main(String[] args) {
        Kryo kryo = new Kryo();

        // There are two ArraysAsListSerializer
        // - when use javakaffee, it works, KeyValueAccess use this way.
        // - when use twitter.chill, it failed, Spark use this way
        kryo.register(Arrays.asList("").getClass(),
                new ArraysAsListSerializer());

        KryoTestDto kryoTestDto = getKryoTestDto();

        Output output = new Output(1000);

        kryo.writeClassAndObject(output, kryoTestDto);

        Input input = new Input(output.getBuffer());

        Object result = kryo.readClassAndObject(input);

        System.out.println(result);
    }

    public static KryoTestDto getKryoTestDto() {
        // here is the critical point, we don't use java.util.ArrayList but java.util.Arrays.ArrayList
        List<Integer> list = Arrays.asList(100);

        KryoTestDto kryoTestDto = new KryoTestDto();

        kryoTestDto.setList(list);

        return kryoTestDto;
    }

    @Data
    static class KryoTestDto {
        private List<Integer> list;
    }
}
