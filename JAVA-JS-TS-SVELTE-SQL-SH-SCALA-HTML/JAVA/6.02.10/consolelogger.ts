import { ILogger } from './loggingutils'

export class ConsoleLogger implements ILogger {
  ok(message: string, messageContext?: object, error?: Error): void {
    console.log(message, messageContext, error)
  }
  debug(message: string, messageContext?: object, error?: Error): void {
    console.debug(message, messageContext, error)
  }
  info(message: string, messageContext?: object, error?: Error): void {
    console.info(message, messageContext, error)
  }
  notice(message: string, messageContext?: object, error?: Error): void {
    console.debug(message, messageContext, error)
  }
  warn(message: string, messageContext?: object, error?: Error): void {
    console.warn(message, messageContext, error)
  }
  error(message: string, messageContext?: object, error?: Error): void {
    console.error(message, messageContext, error)
  }
  critical(message: string, messageContext?: object, error?: Error): void {
    console.error(message, messageContext, error)
  }
  alert(message: string, messageContext?: object, error?: Error): void {
    console.error(message, messageContext, error)
  }
  emerg(message: string, messageContext?: object, error?: Error): void {
    console.error(message, messageContext, error)
  }
}
