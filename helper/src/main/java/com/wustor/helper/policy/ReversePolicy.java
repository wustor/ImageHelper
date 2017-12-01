package com.wustor.helper.policy;


import com.wustor.helper.request.BitmapRequest;


public class ReversePolicy implements  LoadPolicy {
    @Override
    public int compareto(BitmapRequest request1, BitmapRequest request2) {
        return request2.getSerialNo()-request1.getSerialNo();
    }
}
