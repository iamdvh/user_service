package com.iamdvh.UserService.service;

import com.iamdvh.UserService.dto.request.UserRequest;
import com.iamdvh.UserService.dto.response.PageResponse;
import com.iamdvh.UserService.dto.response.UserResponse;
import com.iamdvh.UserService.entity.UserEntity;
import com.iamdvh.UserService.exception.AppException;
import com.iamdvh.UserService.exception.TypeCode;
import com.iamdvh.UserService.mapper.UserMapper;
import com.iamdvh.UserService.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    OTPService otpService;
    public UserResponse create(UserRequest request) throws JOSEException {
        otpService.callStringeeAPI(request.getPhoneNumber());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var user = userMapper.toEntity(request);
        user.setDob(LocalDate.parse(request.getDob()));
        user.setCreateAt(Instant.now());
        UserEntity userNew =  userRepository.save(user);
        UserResponse userResponse = userMapper.toResponse(userNew);
        return userResponse;
    }
    public UserResponse update(String id, UserRequest request){
        UserEntity oldUser =  findById(id);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var userNew = userMapper.toEntity(oldUser,request);
        userNew.setUpdateAt(LocalDate.now());
        return userMapper.toResponse(userRepository.save(userNew));
    }
    public void delete(String id){
        var user = findById(id);
        userRepository.delete(user);
    }
    public PageResponse<UserResponse> findAll(int page, int size){
        Sort sort = Sort.by("username").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = userRepository.findAll(pageable);
        return PageResponse.<UserResponse>builder()
                .page(page)
                .pageTotal(pageData.getTotalPages())
                .sizePage(pageData.getSize())
                .data(pageData.getContent().stream().map(userMapper::toResponse).toList())
                .build();
    }
    UserEntity findById(String id){
        return userRepository.findById(id)
                .orElseThrow(()->new AppException(TypeCode.USER_NOT_FOUND));
    }
    public UserResponse findByIdVer2(String id){
        var user = userRepository.findById(id)
                .orElseThrow(()->new AppException(TypeCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }
}
