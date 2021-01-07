package proxy;

import query.AbstractQuery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import static java.lang.System.*;

public class RunTimeHandler implements InvocationHandler {
    private String prefix;
    private AbstractQuery target;

    public RunTimeHandler(String prefix, AbstractQuery target) {
        this.prefix = prefix;
        this.target = target;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        long start = System.currentTimeMillis();
        List<?> result = (List) method.invoke(this.target, objects);
        long end = System.currentTimeMillis();
        err.println(String.format("%s 用时:\t%d", this.prefix, end - start));
        result.forEach(out::println);
        out.println(String.format("查询到 %d 条记录", result.size()));
        return result;
    }

    public Object createProxy() {
        return Proxy.newProxyInstance(this.target.getClass().getClassLoader(), this.target.getClass().getInterfaces(), this);
    }
}
