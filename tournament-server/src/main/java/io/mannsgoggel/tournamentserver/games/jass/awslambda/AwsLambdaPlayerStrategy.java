package io.mannsgoggel.tournamentserver.games.jass.awslambda;


import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import io.mannsgoggel.gamejass.domain.game.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.game.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.state.State;
import io.mannsgoggel.gamejass.domain.game.strategy.RemotePlayerStrategy;
import lombok.Data;
import lombok.Value;

import java.util.function.Function;

@Data
public class AwsLambdaPlayerStrategy implements RemotePlayerStrategy {
    final RemotePlayerLambdaFunction function = LambdaInvokerFactory.builder()
            .lambdaClient(AWSLambdaClientBuilder.defaultClient())
            .build(RemotePlayerLambdaFunction.class);
    private Function<RemoteAction, Void> onRemoteAction;
    private final String userName;
    private final String strategyCode;

    @Override
    public void nextState(State state) { }

    @Override
    public void requestRemoteAction(RequestRemoteAction action) {
        var result = function.invoke(new AwsLambdaRequest(userName, strategyCode, action));
        onRemoteAction.apply(result);
    }

    @Override
    public void registerOnRemoteAction(Function<RemoteAction, Void> task) {
        this.onRemoteAction = task;
    }

    public interface RemotePlayerLambdaFunction {
        @LambdaFunction(functionName = "mannsgoggel-worker")
        RemoteAction invoke(AwsLambdaRequest request);
    }

    @Value
    static class AwsLambdaRequest {
        String player;
        String strategyCode;
        RequestRemoteAction parameters;
    }
}