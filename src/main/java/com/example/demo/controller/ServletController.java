package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/servlet")
public class ServletController {
    /**
     * 线程池
     */
    public static ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(30);

    @RequestMapping("/orig")
    public void todo(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
        Thread.sleep(100);
        response.getWriter().println("这是【正常】的请求返回");
    }

    /**
     * servlet方式处理异步请求
     * @param request
     * @param response
     */
    @RequestMapping("/async")
    public void todoAsync(HttpServletRequest request,
                          HttpServletResponse response) {
        AsyncContext asyncContext = request.startAsync();
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                log.info("执行完成");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                log.info("超时了：");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) throws IOException {
                log.info("发生错误：", asyncEvent.getThrowable());
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                log.info("线程开始");
            }
        });
        //设置超时时间
        asyncContext.setTimeout(200);
        //也可以不使用start 进行异步调用
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                编写业务逻辑
//
//            }
//        }).start();

        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(100);
                    log.info("内部线程：" + Thread.currentThread().getName());
                    asyncContext.getResponse().setCharacterEncoding("utf-8");
                    asyncContext.getResponse().setContentType("text/html;charset=UTF-8");
                    asyncContext.getResponse().getWriter().println("这是【异步】的请求返回");
                }catch (Exception e) {
                    log.info("异常：", e);
                }
                //异步请求完成通知
                //此时整个请求才完成
                //其实可以利用此特性 进行多条消息的推送 把连接挂起。。
                asyncContext.complete();
            }
        });
        //此时之类 request的线程连接已经释放了
        log.info("线程：" + Thread.currentThread().getName());
    }

    /**
     * Spring方式处理异步请求 callable
     * @return
     */
    @RequestMapping("/callable")
    public Callable<String> callable() {
        log.info("外部线程：" + Thread.currentThread().getName());
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.info("内部线程：" + Thread.currentThread().getName());
                return "callable!";
            }
        };
    }

    /**
     * Spring方式处理异步请求 DeferredResult
     * @return
     */
    @RequestMapping("/deferredResult")
    public DeferredResult<String> deferredResult() {
        log.info("外部线程：" + Thread.currentThread().getName());
        //设置超时时间
        DeferredResult<String> result = new DeferredResult<>(60 * 1000L);
        //处理超时时间 采用委托机制
        result.onTimeout(new Runnable() {
            @Override
            public void run() {
                log.error("DeferredResult超时");
                result.setResult("超时了！");
            }
        });

        result.onCompletion(new Runnable() {
            @Override
            public void run() {
                log.info("调用完成");
            }
        });

        FIXED_THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                //处理业务逻辑
                log.info("内部线程：" + Thread.currentThread().getName());
                //返回结果
                result.setResult("DeferredResult");
            }
        });
        return result;
    }

    /**
     * Spring方式处理异步请求 WebAsyncTask
     * @return
     */
    @RequestMapping("/webAsyncTask")
    public WebAsyncTask<String> webAsyncTask() {
        log.info("外部线程：" + Thread.currentThread().getName());
        WebAsyncTask<String> result = new WebAsyncTask<>(60 * 1000L, new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.info("内部线程：" + Thread.currentThread().getName());
                return "WebAsyncTask!!!";
            }
        });

        result.onTimeout(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "WebAsyncTask超时!!!";
            }
        });

        result.onCompletion(new Runnable() {
            @Override
            public void run() {
                log.info("WebAsyncTask执行结束");
            }
        });

        return result;
    }
}
