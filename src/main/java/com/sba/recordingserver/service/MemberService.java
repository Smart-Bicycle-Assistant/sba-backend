package com.sba.recordingserver.service;

import com.sba.recordingserver.dto.MemberDto;
import com.sba.recordingserver.dto.MemberLoginDto;
import com.sba.recordingserver.dto.MemberPasswordChangeRequestDto;
import com.sba.recordingserver.dto.ResponseDto;
import com.sba.recordingserver.entity.Member;
import com.sba.recordingserver.repository.MemberRepository;
import com.sba.recordingserver.util.Util;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class MemberService {

    @Autowired
    private final MemberRepository memberRepository;
    private JavaMailSender emailSender;


    public ResponseDto registerMember(MemberDto member) {
        Optional<Member> optionalMember = memberRepository.findById(member.getId());
        if(optionalMember.isPresent())
        {
            System.out.println(member.getId()+" is already taken");
            return new ResponseDto("That id is already taken", 406);
        }
        else
        {
            memberRepository.save(member.toEntity());
            System.out.println("register done");
            return new ResponseDto("Register Success",200);
        }
    }


    public ResponseDto handleLoginRequest(MemberLoginDto loginRequest) {
        Optional<Member> optionalMember = memberRepository.findById(loginRequest.getId());
        if(optionalMember.isEmpty())
        {
            System.out.println(loginRequest.getId() + " is not in member db");
            return new ResponseDto("no such id",406);
        }
        else if(!optionalMember.get().getPassword().equals(loginRequest.getPassword()))
        {
            System.out.println("invalid password " + optionalMember.get().getPassword() + " : " + loginRequest.getPassword());
            return new ResponseDto("invalid password",406);
        }
        else
        {
            System.out.println("login request for "+loginRequest.getId() + " successfully done");
            return new ResponseDto("welcome " +optionalMember.get().getNickname(),200);
        }
    }

    public ResponseDto checkDuplicateId(String id)
    {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(optionalMember.isEmpty())
        {
            System.out.println(id + " is not in member db");
            return new ResponseDto("ok",200);
        }
        else
        {
            System.out.println(id + " is already taken");
            return new ResponseDto("duplicate",406);
        }
    }

    public ResponseDto resetPassword(String id)
    {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(optionalMember.isEmpty())
        {
            System.out.println(id+" is not in member db");
            return new ResponseDto("no such id",406);
        }
        else
        {
            String password = Util.createPassword();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ywha0929@gmail.com");
            message.setTo(optionalMember.get().getEmail());
            message.setSubject("You've reseted your password");
            message.setText("Your request for reseting your password has been successfully done\n" +
                    "Your new password is " +password + "\n"+
                    "Please change your password immediately when you login\n"+
                    "Thank you\n");
            Member member = optionalMember.get();
            member.setPassword(password);
            memberRepository.save(member);
            emailSender.send(message);
            return new ResponseDto("Your new password has been sent to your email",200);
        }
    }

    public ResponseDto changePassword(MemberPasswordChangeRequestDto request)
    {
        Optional<Member> optionalMember = memberRepository.findById(request.getId());
        if(optionalMember.isEmpty())
        {
            System.out.println(request.getId()+" is not in member db");
            return new ResponseDto("no such id",406);
        }
        else
        {
            Member member = optionalMember.get();
            if(!member.getPassword().equals(request.getPassword())) //wrong password
            {
                System.out.println("invalid password " + optionalMember.get().getPassword() + " : " + request.getPassword());
                return new ResponseDto("invalid password",406);
            }
            else if(member.getPassword().equals(request.getNewPassword())) //new password old password same
            {
                System.out.println("new password old password same" + optionalMember.get().getPassword() + " : " + request.getNewPassword());
                return new ResponseDto("new password is same as before",406);
            }
            else
            {
                member.setPassword(request.getNewPassword());
                memberRepository.save(member);
                return new ResponseDto("your new password has been successfully changed",200);
            }
        }
    }
}
