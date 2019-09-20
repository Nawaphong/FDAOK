package com.cdg.fdaok;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

public class ProductDetailMBS extends BottomSheetDialogFragment {
//    private ProductDetailListender mListenner;
    private DataEntity entity;
    ProductDetailMBS(DataEntity entity) {
        this.entity = entity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.product_detail,container,false);

//        String mystring = getResources().getString(R.string.mystring);

        TextView mcncnmTxt = v.findViewById(R.id.cncnm_txt);
        TextView mtypeproTxt = v.findViewById(R.id.typepro_txt);
        TextView mlcnnoTxt = v.findViewById(R.id.lcnno_txt);
        TextView mproducthaTxt = v.findViewById(R.id.productha_txt);
        TextView mproducengTxt = v.findViewById(R.id.produceng_txt);
        TextView mlicenTxt = v.findViewById(R.id.licen_txt);
        TextView mthanamTxt = v.findViewById(R.id.thanm_txt);
//        TextView mAddrTxt = v.findViewById(R.id.Addr_txt);
//        TextView mNewCodeTxt = v.findViewById(R.id.NewCode_txt);

        mcncnmTxt.setText(entity.getCncnm());
        mtypeproTxt.setText(entity.getTypepro());
        mlcnnoTxt.setText(entity.getLcnno());
        mproducthaTxt.setText(entity.getProductha());
        mproducengTxt.setText(entity.getProduceng());
        mlicenTxt.setText(entity.getLicen());
        mthanamTxt.setText(entity.getThanm());
//        mAddrTxt.setText(entity.getAddr());
//        mNewCodeTxt.setText(entity.getNewCode());

        Button closeBtn = v.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListenner.onHistory();
                dismiss();
            }
        });
        return v;
    }

//    public interface ProductDetailListender {
//        void onHistory();
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            mListenner = (ProductDetailListender) context;
//        }catch (ClassCastException e){
//            throw new ClassCastException(context.toString() + "must implement ProductDetailListener");
//        }
//
//    }
}
