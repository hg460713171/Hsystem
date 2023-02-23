package com.hs.hsadmin;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class HsDynamicProxy {
    public static void main(String[] args) {
        HsTest hs = new HsTest();
        Object ob = Proxy.newProxyInstance(
                hs.getClass().getClassLoader(), // 目标类的类加载
                hs.getClass().getInterfaces(),  // 代理需要实现的接口，可指定多个
                new myInvocationHandler(hs)); // 代理对象对应的自定义 InvocationHandler
        HsTestIterface s =  (HsTestIterface)ob;
        s.sayHello();

        Enhancer enhancer = new Enhancer();
        // 设置类加载器
        enhancer.setClassLoader(hs.getClass().getClassLoader());
        // 设置被代理类
        enhancer.setSuperclass(hs.getClass());
        // 设置方法拦截器
        enhancer.setCallback(new MyMethodInterceptor());
        // 创建代理类
        HsTestIterface hsTestIterface = (HsTestIterface) enhancer.create();
        hsTestIterface.sayHello();
    }



}


class MyMethodInterceptor implements MethodInterceptor{

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("before method " + method.getName());
        Object object = methodProxy.invokeSuper(o, objects);
        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("after method " + method.getName());
        return object;
    }
}

interface HsTestIterface{
    void sayHello();
}
class HsTest implements HsTestIterface {


    @Override
    public void sayHello() {
        System.out.println("111");

    }
}
class myInvocationHandler implements InvocationHandler{

    private Object object;

    public myInvocationHandler(Object object){
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before invoke "  + method.getName());
        method.invoke(object, args);
        System.out.println("After invoke " + method.getName());
        return null;
    }

}

