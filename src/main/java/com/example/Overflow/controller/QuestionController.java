package com.example.Overflow.controller;

import com.example.Overflow.model.*;

import com.example.Overflow.service.AnswerService;
import com.example.Overflow.service.QuestionService;

import com.example.Overflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/query")
public class QuestionController {

    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @GetMapping("/ans")
    public  List<Answer> getAnswerList(){
        return answerService.allAnswers();
    }

    @GetMapping("/ans/{id}")
    public ResponseEntity<Answer> getAns(@PathVariable Integer id) {
        try {
            Answer answer = answerService.getById(id);
            return new ResponseEntity<Answer>(answer, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<Answer>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/ansbyQuesId/{id}")
    public ResponseEntity<List<Answer>> getAnsByQuesId(@PathVariable Integer id) {
        try {
            List<Answer> answerList = new ArrayList<>();
            Question question = questionService.getById(id);
            String answerIds = question.getAnswerId();
            String[] answerIdArray = answerIds.split(",");
            for(String ansId : answerIdArray){
                Answer answer = answerService.getById(Integer.valueOf(ansId));
                answerList.add(answer);

            }

            return new ResponseEntity<List<Answer>>(answerList, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<List<Answer>>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/addAns")
    public Answer addAns(@RequestBody AnswerRequest answer) {
        try {
            // Retrieve the corresponding question
            Question question = questionService.getById(answer.getQuesId());

            // Create and save the answer
            Answer ans = new Answer();
            Date createDate = new Date();
            ans.setAnswer_desc(answer.getAnsDesc());
            ans.setCreateDate(createDate);
            ans.setFirstName(question.getFirstName());
            ans.setLastName(question.getLastName());
            ans.setQuesId(question.getId());
            ans.setUserId(Integer.valueOf(question.getUserId()));
            ans.setQuest_desc(question.getQuesDesc());
            answerService.saveAns(ans);

            // Update the answerIds of the question
            String answerIds = question.getAnswerId();
            if (Objects.isNull(answerIds)) {
                answerIds = String.valueOf(ans.getId());
            } else {
                answerIds = String.join(",", answerIds, String.valueOf(ans.getId()));
            }
            question.setAnswerId(answerIds);
            questionService.saveQues(question);

            System.out.println("Posting answer");
            return ans;
        } catch (Exception e) {
            // Handle exceptions, log the error, and return an appropriate response
            e.printStackTrace();
            throw new RuntimeException("Failed to add answer: " + e.getMessage());
        }
    }

    @GetMapping("/ques")
    public List<Question> list() {
        System.out.println("getting Quest");
        return questionService.allQuestion();
    }

    @GetMapping("/ques/{id}")
    public ResponseEntity<Question> get(@PathVariable Integer id) {
        try {
            Question question = questionService.getById(id);
            return new ResponseEntity<Question>(question, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<Question>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/addques")
    public ResponseEntity<Question> add(@RequestBody AskQuestionModel askQuestionModel) {
        try {
            User user = userService.findUserByEmail(askQuestionModel.getEmail());
            //User user = userService.getUser(Integer.valueOf(ques.getUserId()));
            System.out.println(askQuestionModel.getEmail());
            Question ques = new Question();
            System.out.println(user.getEmail());
            ques.setQuesDesc(askQuestionModel.getQuesDesc());
            ques.setQuesTitle(askQuestionModel.getQuesTitle());
            ques.setFirstName(user.getfirstName());
            ques.setLastName(user.getlastName());
            ques.setUserId(String.valueOf(user.getId()));
            Date createDate = new Date();
            ques.setCreateDate(createDate);

            System.out.println("adding ques" + ques.getUserId() + ques.getQuesDesc() + ques.getQuesDesc());

            questionService.saveQues(ques);
            return new ResponseEntity<Question>(ques,HttpStatus.OK);
        }catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity<Question>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("updateques/{id}")
    public ResponseEntity<?> update(@RequestBody Question ques, @PathVariable Integer id) {
        try {
            Question existQues = questionService.getById(id);
            ques.setId(id);

            questionService.saveQues(ques);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}