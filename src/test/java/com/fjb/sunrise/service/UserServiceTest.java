package com.fjb.sunrise.service;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.exceptions.DuplicatedException;
import com.fjb.sunrise.mappers.UserMapper;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {
    @MockBean
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Value("${default.admin-create-key}")
    private String adminCreateKey;

    private RegisterRequest request;

    @BeforeEach
    void init() {
        request = Instancio.of(RegisterRequest.class)
            .generate(field(RegisterRequest::getEmail), x -> x.net().email()) // generate value base on Instancio library
            .supply(field(RegisterRequest::getPhone), x -> "0" + x.digits(9)) // generate value follow developer
            .set(field(RegisterRequest::getPassword), "123456aA@") // set value into specific field. If Being list, setting all
            .create();
    }

    //FunctionName_WhenDataHow_ThenResultHow
    @Test
    void checkRegister_WhenDataIsNormal_ThenResultWillReturn200WithResponse(){
        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(false);

        final String actualResult = userService.checkRegister(request);

        Assertions.assertNull(actualResult);
    }

    @Test
    void checkRegister_WhenEmailFieldIsExisted_ThenResultWillReturnException() {
        User user = new User();
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(Instancio.of(String.class).supply(Select.allStrings(), x-> "0" + x.digits(9)).create());

        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(true);

        Exception duplicatedException = assertThrows(DuplicatedException.class, () -> {
                userService.checkRegister(request);
            });
        Assertions.assertEquals("Email hoặc số điện thoại đã được đăng ký!", duplicatedException.getMessage());
    }

    @Test
    void checkRegister_WhenPhoneFieldIsExisted_ThenResultWillReturnException() {
        User user = new User();
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setEmail(Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create());

        Mockito.when(userRepository.save(user)).thenReturn(user);

        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(true);

        Exception duplicatedException = assertThrows(DuplicatedException.class, () -> {
            userService.checkRegister(request);
        });
        Assertions.assertEquals("Email hoặc số điện thoại đã được đăng ký!", duplicatedException.getMessage());
    }

    @Test
    void checkRegister_WhenRegisterWithAdminKey_ThenResultWillReturnUserHasRoleAdmin() {
        request.setPassword(Instancio.of(String.class).supply(Select.allStrings(), x-> adminCreateKey + anyString()).create());
        Mockito.when(userRepository.findByEmailOrPhone(eq(request.getEmail()))).thenReturn(null);

        userService.checkRegister(request);
        User user = userRepository.findByEmailOrPhone(eq(request.getEmail()));
        Assertions.assertEquals(ERole.ADMIN, user.getRole());
    }
}
