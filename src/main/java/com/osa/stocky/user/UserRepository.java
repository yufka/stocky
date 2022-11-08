package com.osa.stocky.user;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository access user Information from DB. Relays on {@link JpaRepository}
 * and contains few custom methods that allow to access used by API-key and also update user subscription plan.
 * 
 * @author oleksii
 * @since 5 Nov 2022
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(nativeQuery = true, name = "find_by_name_and_password")
    public User get(
            @Param(value = "name") String name,
            @Param(value = "password") String password);
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, name = "update_subscription_by_api_key")
    public void updateSubscription(
            @Param(value = "name") String apikey, 
            @Param(value = "password") String password,
            @Param(value = "subid") int subscriptionId);
}
