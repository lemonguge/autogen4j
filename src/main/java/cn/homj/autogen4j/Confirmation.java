package cn.homj.autogen4j;

/**
 * @author jiehong.jh
 * @date 2023/11/29
 */
@FunctionalInterface
public interface Confirmation {

    Confirmation ALWAYS_TRUE = () -> true;

    boolean get();
}
