package net.sourceforge.cobertura;

import org.apache.log4j.Logger;

public class LoggerWrapper {
    private final Logger log = Logger.getLogger(LoggerWrapper.class);

		private boolean failOnError = false;

		public void setFailOnError(boolean failOnError){
			this.failOnError = failOnError;
		}

		public void debug(String message){
			log.debug(message);
		}

		public void debug(String message, Throwable t){
			log.debug(message, t);
		}

		public void warn(String message, Throwable t) throws Throwable{
			log.warn(message, t);
			if (failOnError){
				throw t;
			}
		}

        public void info(String message){
            log.info(message);
        }

        public void error(String message){
            log.error(message);
        }
}
