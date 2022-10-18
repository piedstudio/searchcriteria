package com.piedstudio.searchcriteria;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class SearchCriteria {
    public static Predicate getPredicate(CriteriaBuilder builder, Root<?> root, SearchCriteria criteria) {
        List<Predicate> predicates = new ArrayList<>();
        Field[] fields = criteria.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(criteria);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null) continue;
            if (Collection.class.isAssignableFrom(field.getType())) {
                Object[] collection = (new ArrayList<>((Collection<?>) value)).toArray();
                Predicate[] sub = new Predicate[collection.length];
                for (int i = 0; i < collection.length; i++) {
                    sub[i] = getPredicate(builder, root, field, collection[i]);
                }
                predicates.add(builder.or(sub));
            } else predicates.add(getPredicate(builder, root, field, value));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private static Predicate getPredicate(CriteriaBuilder builder, Root<?> root, Field field, Object value) {
        Path<Object> f = root.get(field.getName());
        if (value instanceof String) {
            if (f.getJavaType().getName().equals(Timestamp.class.getName())) {
                return getPredicateForTimestamp(builder, root, field, (String) value);
            } else if (f.getJavaType().getName().equals(Integer.class.getName())) {
                return getPredicateForInteger(builder, root, field, (String) value);
            } else if (f.getJavaType().getName().equals(Long.class.getName())) {
                return getPredicateForLong(builder, root, field, (String) value);
            } else if (f.getJavaType().getName().equals(Double.class.getName())) {
                return getPredicateForDouble(builder, root, field, (String) value);
            } else if (f.getJavaType().getName().equals(String.class.getName())) {
                return getPredicateForString(builder, root, field, (String) value);
            }
        }
        return builder.equal(root.get(field.getName()), value);
    }

    private static Predicate getPredicateForTimestamp(CriteriaBuilder builder, Root<?> root, Field field, String value) {
        String[] parts = value.split("\\|");
        Timestamp vT = Timestamp.from(Instant.parse(parts.length == 2 ? parts[1] : value));
        Path<Timestamp> fT = root.get(field.getName());
        if (parts.length == 2) {
            switch (parts[0]) {
                case "greaterThan":
                    return builder.greaterThan(fT, vT);
                case "greaterThanOrEqualTo":
                    return builder.greaterThanOrEqualTo(fT, vT);
                case "lessThan":
                    return builder.lessThan(fT, vT);
                case "lessThanOrEqualTo":
                    return builder.lessThanOrEqualTo(fT, vT);
            }
        }
        return builder.equal(fT, vT);
    }

    private static Predicate getPredicateForInteger(CriteriaBuilder builder, Root<?> root, Field field, String value) {
        String[] parts = value.split("\\|");
        Integer vT = Integer.parseInt(parts.length == 2 ? parts[1] : value);
        Path<Integer> fT = root.get(field.getName());
        if (parts.length == 2) {
            switch (parts[0]) {
                case "greaterThan":
                    return builder.greaterThan(fT, vT);
                case "greaterThanOrEqualTo":
                    return builder.greaterThanOrEqualTo(fT, vT);
                case "lessThan":
                    return builder.lessThan(fT, vT);
                case "lessThanOrEqualTo":
                    return builder.lessThanOrEqualTo(fT, vT);
            }
        }
        return builder.equal(fT, vT);
    }

    private static Predicate getPredicateForLong(CriteriaBuilder builder, Root<?> root, Field field, String value) {
        String[] parts = value.split("\\|");
        Long vT = Long.parseLong(parts.length == 2 ? parts[1] : value);
        Path<Long> fT = root.get(field.getName());
        if (parts.length == 2) {
            switch (parts[0]) {
                case "greaterThan":
                    return builder.greaterThan(fT, vT);
                case "greaterThanOrEqualTo":
                    return builder.greaterThanOrEqualTo(fT, vT);
                case "lessThan":
                    return builder.lessThan(fT, vT);
                case "lessThanOrEqualTo":
                    return builder.lessThanOrEqualTo(fT, vT);
            }
        }
        return builder.equal(fT, vT);
    }

    private static Predicate getPredicateForDouble(CriteriaBuilder builder, Root<?> root, Field field, String value) {
        String[] parts = value.split("\\|");
        Double vT = Double.parseDouble(parts.length == 2 ? parts[1] : value);
        Path<Double> fT = root.get(field.getName());
        if (parts.length == 2) {
            switch (parts[0]) {
                case "greaterThan":
                    return builder.greaterThan(fT, vT);
                case "greaterThanOrEqualTo":
                    return builder.greaterThanOrEqualTo(fT, vT);
                case "lessThan":
                    return builder.lessThan(fT, vT);
                case "lessThanOrEqualTo":
                    return builder.lessThanOrEqualTo(fT, vT);
            }
        }
        return builder.equal(fT, vT);
    }

    private static Predicate getPredicateForString(CriteriaBuilder builder, Root<?> root, Field field, String value) {
        String[] parts = value.split("\\|");
        String vT = parts.length == 2 ? parts[1] : value;
        Path<String> fT = root.get(field.getName());
        if (parts.length == 2) {
            switch (parts[0]) {
                case "startsWith":
                    return builder.like(fT, vT + "%");
                case "endsWith":
                    return builder.like(fT, "%" + vT);
                case "contains":
                    return builder.like(fT, "%" + vT + "%");
                case "iStartsWith":
                    return builder.like(builder.lower(fT), vT.toLowerCase() + "%");
                case "iEndsWith":
                    return builder.like(builder.lower(fT), "%" + vT.toLowerCase());
                case "iContains":
                    return builder.like(builder.lower(fT), "%" + vT.toLowerCase() + "%");
            }
        }
        return builder.equal(fT, vT);
    }
}
