package com.demo.showcase.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VoidHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.showcase.verticle.DemoWorker;


public class Starter extends AbstractVerticle {
   private Logger log = LoggerFactory.getLogger( getClass() );

   @Override
   public void start( final Future<Void> startedResult ) {
      try {
         startWorker( new VoidHandler() {
            @Override
            public void handle() {
               startedResult.complete();
            }
         } );
      } catch ( Exception e ) {
         startedResult.fail( e );
      }
   }

   private void startWorker( final Handler<Void> resultHandler ) {

      Handler<AsyncResult<String>> handler = ( event ) -> {
         if ( event.succeeded() ) {
            log.debug( "Successfully instantiated worker" );
         } else {
            log.error( "Failed to instantiate worker" );

            setUpRetry();
         }

         resultHandler.handle( null );
      };

      deployWorker( handler );
   }

   private void setUpRetry() {
      final Handler<Long> timerHandler = ( schedulerId ) -> {
         log.info( "Retrying to start the worker." );

         Handler<AsyncResult<String>> reconnectionHandler = ( result ) -> {
            if ( result.succeeded() ) {
               log.info( "Successfully set up the worker" );
            } else {
               log.error( "Failed deploying worker." );
               setUpRetry();
            }
         };

         try {
            deployWorker( reconnectionHandler );
         } catch ( Exception e ) {
            log.error( "Error starting worker. Will retry.", e );
         }
      };

      vertx.setTimer( 3000, timerHandler );
   }

   private void deployWorker( Handler<AsyncResult<String>> reconnectionHandler ) {
      DeploymentOptions options = new DeploymentOptions().setWorker( true ).setInstances( 1 );
      vertx.deployVerticle( DemoWorker.class.getName(), options, reconnectionHandler );
   }
}
