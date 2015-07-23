package com.demo.showcase.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DemoWorker extends AbstractVerticle {

   private Logger logger = LoggerFactory.getLogger( getClass() );

   @Override
   public void start( final Future<Void> startedResult ) {
      logger.debug( "Starting worker " + this );

      try {
         // ...
         throw new Exception( "Something unwanted happened." );
      } catch ( Exception e ) {
         logger.error( "Failed" );
         startedResult.fail( e );
         return;
      }

      // ... startedResult.complete();
   }

   @Override
   public void stop() {
      logger.info( "Stopping worker! " + this );
   }
}
