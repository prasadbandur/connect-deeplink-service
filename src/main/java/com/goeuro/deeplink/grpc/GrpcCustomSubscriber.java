package com.goeuro.deeplink.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@RequiredArgsConstructor
public class GrpcCustomSubscriber<T> implements Subscriber<T> {

  private final StreamObserver<? super T> grpcStreamObserver;

  @Override
  public void onSubscribe(Subscription subscription) {
    subscription.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(T t) {
    grpcStreamObserver.onNext(t);
  }

  @Override
  public void onError(Throwable throwable) {
    grpcStreamObserver.onError(throwable);
  }

  @Override
  public void onComplete() {
    grpcStreamObserver.onCompleted();
  }
}
