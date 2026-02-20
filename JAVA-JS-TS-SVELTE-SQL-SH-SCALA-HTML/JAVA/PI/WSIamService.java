package it.postecom.registrazione;

import org.jod.base.log.ILoggerService;
import org.jod.base.message.IMessage;

import it.postecom.ws.iam.ged.command.DeleteInsertAnagraphicTelephoneNumberCommand;
import it.postecom.ws.iam.ged.command.EnableUserCommand;
import it.postecom.ws.iam.ged.command.InsertAnagraphicTelephoneNumberCommand;
import it.postecom.ws.iam.ged.command.ValidateAnagraphicTelephoneNumberOccurrencesCommand;
import it.postecom.ws.iam.ged.dto.BooleanCommandOutput;
import it.postecom.ws.iam.ged.dto.ClientCommandOutput;
import it.postecom.ws.iam.ged.dto.TelephoneNumberInput;
import it.postecom.ws.iam.ged.dto.TelephoneNumberOutput;

public class WSIamService implements IWSIamService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _name;
	private IMessage _message;
	private String _className = getClass().getSimpleName();

	public WSIamService(String name, ILoggerService loggerService) {
		_name = name;
		_message = new WSIamMessage(loggerService.message());
	}

	public String name() {
		return _name;
	}

	public boolean insertAnagraphicTelephoneNumber(TelephoneNumberInput input, TelephoneNumberOutput output) {
		InsertAnagraphicTelephoneNumberCommand command = new InsertAnagraphicTelephoneNumberCommand(input, output);
		_message.debug(_className + ".insertAnagraphicTelephoneNumber - before call ws iam service insertAnagraphicTelephoneNumber for number: " + input.getPrefix() + "" + input.getTelephoneNumber());
		boolean exec = command.exec();
		_message.debug(_className + ".insertAnagraphicTelephoneNumber - after call ws iam service insertAnagraphicTelephoneNumber - result: " + exec);
		return exec;
	}

	public boolean deleteAnagraphicTelephoneNumber(TelephoneNumberInput input, TelephoneNumberOutput output) {
		DeleteInsertAnagraphicTelephoneNumberCommand command = new DeleteInsertAnagraphicTelephoneNumberCommand(input, output);
		_message.debug(_className + ".deleteAnagraphicTelephoneNumber - before call ws iam service for rollback deleteAnagraphicTelephoneNumber for number: " + input.getPrefix() + "" + input.getTelephoneNumber());
		boolean exec = command.exec();
		_message.debug(_className + ".deleteAnagraphicTelephoneNumber - after call ws iam service for rollback service deleteAnagraphicTelephoneNumber - result: " + exec);
		return exec;
	}

	public boolean validateAnagraphicTelephoneNumberOccurrences(TelephoneNumberInput input, BooleanCommandOutput output) {
		ValidateAnagraphicTelephoneNumberOccurrencesCommand command = new ValidateAnagraphicTelephoneNumberOccurrencesCommand(input, output);
		_message.debug(_className + ".validateAnagraphicTelephoneNumberOccurrences - before call ws iam service for rollback validateAnagraphicTelephoneNumberOccurrences for number: " + input.getPrefix() + "" + input.getTelephoneNumber());
		boolean exec = command.exec();
		_message.debug(_className + ".validateAnagraphicTelephoneNumberOccurrences - after call ws iam service for rollback service validateAnagraphicTelephoneNumberOccurrences - result: " + exec);
		return exec;
	}

	public boolean enableUser(String input, ClientCommandOutput output) {
		EnableUserCommand command = new EnableUserCommand(input, output);
		_message.debug(_className + ".enableUser - before call ws iam service for rollback enableUser for user: " + input);
		boolean exec = command.exec();
		_message.debug(_className + ".enableUser - after call ws iam service for rollback service enableUser - result: " + exec);
		return exec;
	}

	public void start() throws Exception {
	}

	public void stop() throws Exception {
	}
}
