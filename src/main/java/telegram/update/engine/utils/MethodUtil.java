package telegram.update.engine.utils;

import java.util.List;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodUtil {

	public static String prepareMethodPath(String pathToMethod, String methodArgs) {
		StringBuilder path = new StringBuilder(pathToMethod);

		path.insert(path.lastIndexOf("."), ")");
		path.append(methodArgs);
		path.deleteCharAt(path.lastIndexOf(","));

		return path.toString();
	}

	public static Object proccessMethod(String basePath, String endPath, List<Object> arguments) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
		StringBuilder args = new StringBuilder();

		arguments.stream().forEach(arg -> {
			String variableName = arg.getClass().getSimpleName().toLowerCase();
			evaluationContext.setVariable(variableName, arg);
			args.append(String.format("#%s,", variableName));
		});

		String methodPath = prepareMethodPath(String.format("T(%s%s", basePath, endPath),
				String.format("(%s)", args.toString()));
		return parser.parseExpression(methodPath).getValue(evaluationContext);
	}
}
