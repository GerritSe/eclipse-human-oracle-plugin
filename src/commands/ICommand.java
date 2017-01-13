package commands;

public interface ICommand<T> {
	public T call();
}
