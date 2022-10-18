## Search Criteria
Consider a situation where you have a JPA entity `User` with a number of fields and a `GET` API endpoint that returns the list of `users`. Adding parameters to the endpoint to allow filtering quickly becomes repetitive and cumbersome especially if there are a lot of parameters. In order to implement this in a concise manner, you can follow the example below. 
- Define entity

```java
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@javax.persistence.Entity
public class User {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private Timestamp createdAt;
    private Boolean isActive;

    // getters & setters
}
```

- Define search criteria

```java
import com.piedstudio.searchcriteria.SearchCriteria;

import java.sql.Timestamp;

public class UserSearchCriteria extends SearchCriteria {
    private Integer id;
    private String firstName;
    private String lastName;
    private Timestamp createdAt;
    private Boolean isActive;
    
    // getters & setters
}
```

- Define controller
```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@RestController
public class UserController {
    @PersistenceContext
    private EntityManager entityManager;
    
    @GetMapping("/users")
    public ResponseEntity<User> getUsers(@RequestParam UserSearchCriteria userSearchCriteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.from(User.class);
        query.where(SearchCriteria.getPredicate(builder, root, userSearchCriteria));
        query.select(root);
        query.distinct(true);
        return ResponseEntity.ok(entityManager.createQuery(query).getResultList());
    }
}
```