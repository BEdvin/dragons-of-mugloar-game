package com.bigbank.game.converter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConverter<Target, Result> {

    public abstract Result convert(Target target);

    public List<Result> convert(Target[] target) {
        if (target == null) {
            return null;
        }
        List<Result> result = new ArrayList<>();
        for (Target document : List.of(target)) {
            result.add(convert(document));
        }
        return result;
    }
}
