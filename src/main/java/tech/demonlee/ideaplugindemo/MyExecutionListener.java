package tech.demonlee.ideaplugindemo;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.ui.Messages;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Demon.Lee
 * @date 2023-06-16 13:51
 */
public class MyExecutionListener implements ExecutionListener {

    @Override
    public void processStarting(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        String projectName = env.getProject().getName();
        String msg = executorId + ", " + projectName + ", " + env.getModulePath();
        Messages.showInfoMessage("Server 要开始启动了......" + msg, "Server Starting");
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env,
                               @NotNull ProcessHandler handler) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://baidu.com")
                .build();

        int statusCode = 0;
        try {
            Response response = client.newCall(request).execute();
            statusCode = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Messages.showInfoMessage("Server 启动好了......" + statusCode, "Server Started");
    }
}
