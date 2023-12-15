package cn.homj.autogen4j.support;

import java.util.Arrays;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import cn.homj.autogen4j.AgentFunction;

import static java.util.Collections.singletonList;

/**
 * @author jiehong.jh
 * @date 2023/11/30
 */
public class AgentFunctions {

    public static final String BOOK_RESTAURANT = "book_restaurant";
    public static final String GET_CURRENT_WEATHER = "get_current_weather";
    public static final String GET_WEATHER_FORECAST = "get_n_day_weather_forecast";

    public static AgentFunction bookRestaurant() {
        return new AgentFunction()
            .name(BOOK_RESTAURANT)
            .description("预订餐厅")
            .parameters(new JSONObject()
                .fluentPut("type", "object")
                .fluentPut("properties", new JSONObject()
                    .fluentPut("name", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("description", "餐厅名称，例如：茶人村")
                    )
                    .fluentPut("dining_time", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("description", "用餐时间，例如：2023-12-05 19:45")
                    )
                    .fluentPut("number_of_diners", new JSONObject()
                        .fluentPut("type", "integer")
                        .fluentPut("description", "用餐人数")
                    )
                )
                .fluentPut("required", Arrays.asList("name", "dining_time", "number_of_diners"))
            )
            .action(input -> "success");
    }

    public static AgentFunction getCurrentWeather() {
        return new AgentFunction()
            .name(GET_CURRENT_WEATHER)
            .description("Get the current weather in a given location")
            .parameters(new JSONObject()
                .fluentPut("type", "object")
                .fluentPut("properties", new JSONObject()
                    .fluentPut("location", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("description", "The city and state, e.g. San Francisco, CA")
                    )
                    .fluentPut("unit", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("enum", Arrays.asList("celsius", "fahrenheit"))
                    )
                )
                .fluentPut("required", singletonList("location"))
            )
            .parser(JSON::parseObject)
            .action(input -> {
                JSONObject arguments = (JSONObject)input;
                String location = arguments.getString("location");
                String unit = arguments.getString("unit");
                if (unit == null) {
                    unit = "celsius";
                }
                return new JSONObject()
                    .fluentPut("location", location)
                    .fluentPut("unit", unit)
                    .fluentPut("temperature", "25.7")
                    .fluentPut("description", "sunny");
            });
    }

    public static AgentFunction getWeatherForecast() {
        return new AgentFunction()
            .name(GET_WEATHER_FORECAST)
            .description("Get an N-day weather forecast")
            .parameters(new JSONObject()
                .fluentPut("type", "object")
                .fluentPut("properties", new JSONObject()
                    .fluentPut("location", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("description", "The city and state, e.g. San Francisco, CA")
                    )
                    .fluentPut("unit", new JSONObject()
                        .fluentPut("type", "string")
                        .fluentPut("enum", Arrays.asList("celsius", "fahrenheit"))
                    )
                    .fluentPut("num_days", new JSONObject()
                        .fluentPut("type", "integer")
                        .fluentPut("description", "The number of days to forecast")
                    )
                )
                .fluentPut("required", Arrays.asList("location", "num_days"))
            )
            .parser(JSON::parseObject)
            .action(input -> {
                JSONObject arguments = (JSONObject)input;
                String location = arguments.getString("location");
                String unit = arguments.getString("unit");
                if (unit == null) {
                    unit = "celsius";
                }
                return new JSONObject()
                    .fluentPut("location", location)
                    .fluentPut("unit", unit)
                    .fluentPut("temperature", "25.7")
                    .fluentPut("description", "sunny");
            });
    }
}
