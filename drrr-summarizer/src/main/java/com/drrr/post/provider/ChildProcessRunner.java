package com.drrr.post.provider;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;

@Component
public class ChildProcessRunner {


    public String execute(String... commands) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            Process process = processBuilder.start();

            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine;
            var sb = new StringBuilder();
            while ((outputLine = processOutputReader.readLine()) != null) {
                sb.append(outputLine);
            }

            // 프로세스가 종료될 때까지 대기
            process.waitFor();
            return sb.toString();
        } catch (Exception exception) {
            throw new RuntimeException("하위 명령어를 실행할 수 없습니다.");
        }
    }
}
