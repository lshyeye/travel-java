package com.example.traveljava.controller;


import com.example.traveljava.dto.ChatRequestDTO;
import com.example.traveljava.dto.TravelRequestDTO;
import com.example.traveljava.service.TravelService;
import com.example.traveljava.vo.Result;
import com.example.traveljava.vo.travelRecommendVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 定义接口路径和请求类型 注解
//定义的返回内容为json格式，需要添加@RestController注解
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;
    @GetMapping("/hello")
    //   String 代表返回值类型
   public Result<String> hello(){

      //       return "hello world";
      return Result.ok("hello world");
   }

   @PostMapping("/recommend")
   //   travelRecommendVO 代表返回值类型 , 需要接受前端的传参
   public Result<travelRecommendVO> recommend(@Valid @RequestBody TravelRequestDTO travelRequestDTO){
//      System.out.println(travelRequestDTO.getCity());
//      System.out.println(travelRequestDTO.getDays());
//      System.out.println(travelRequestDTO.getBudget());
      travelRecommendVO travelRecommendVO = travelService.recommend(travelRequestDTO.getCity(), travelRequestDTO.getDays(), travelRequestDTO.getBudget());
      return Result.ok(travelRecommendVO);
   }

   @PostMapping(value = "/chat", produces = "text/event-stream")
    public SseEmitter chat(@Valid @RequestBody ChatRequestDTO chatRequestDTO) {
        return null;
   }

}