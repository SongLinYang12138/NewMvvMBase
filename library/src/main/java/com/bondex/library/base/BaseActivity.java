package com.bondex.library.base;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.bondex.library.R;
import com.bondex.library.app.PhotoApplication;
import com.bondex.library.ui.IconText;
import com.bondex.library.util.CommonUtils;
import com.bondex.library.util.NoDoubleClickListener;

import com.bondex.library.util.StatusBarUtil;
import com.bondex.library.util.ToastUtils;

import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.bondex.library.app.PhotoApplication.getContext;

/**
 * date: 2020/4/23
 *
 * @Author: ysl
 * description:
 */
public abstract class BaseActivity<D extends ViewDataBinding, M extends BaseViewMode> extends AppCompatActivity {

    protected D binding;
    protected M viewModel;

    protected IconText itBack, itRight;
    protected TextView tvTitle;
    protected AVLoadingIndicatorView loading;

    private Observer<Boolean> loadingObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isShow) {

            if (loading != null) {
                loading.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }


        }
    };
    private Observer<String> toastObserver = new Observer<String>() {
        @Override
        public void onChanged(String s) {

            showDialog(s);
        }
    };

    private Observer<String> msgObserver = new Observer<String>() {
        @Override
        public void onChanged(String s) {

            handleMsg(s);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        initViewModel();
        performDataBinding();
        initView();
        initListener();

    }

    @Override
    protected void onStart() {
        super.onStart();

        initObserver();
    }

    private void initStatusBar() {

        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeObserver();
        viewModel.detachUi();
    }

    private void performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        View view = binding.getRoot();

        itBack = view.findViewById(R.id.title_it_back);
        tvTitle = view.findViewById(R.id.title_tv_title);
        itRight = view.findViewById(R.id.title_it_right);
        loading = view.findViewById(R.id.avl_loading);

        if (getBindingVariable() > 0) {
            binding.setVariable(getBindingVariable(), viewModel);
        }
        binding.executePendingBindings();
    }

    private void initViewModel() {

        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            modelClass = BaseViewMode.class;
        }
        viewModel = (M) createViewModel(this, modelClass);
        viewModel.attachUi();
        viewModel.setContext(getContext());
//生命周期监听
        getLifecycle().addObserver(viewModel);

    }

    private void initObserver() {

        viewModel.loading.observe(this, loadingObserver);
        viewModel.toastLiveData.observe(this, toastObserver);
        viewModel.msgLiveData.observe(this,msgObserver);
    }

    private void removeObserver() {

        if (viewModel.loading.hasObservers()) {
            viewModel.loading.removeObserver(loadingObserver);
        }
        if (viewModel.toastLiveData.hasObservers()) {
            viewModel.toastLiveData.removeObserver(toastObserver);
        }
        if(viewModel.msgLiveData.hasObservers()){
            viewModel.msgLiveData.removeObserver(msgObserver);
        }
        getLifecycle().removeObserver(viewModel);

    }

    protected void showLeft(boolean isShow, int resourceId) {

        if (itBack != null) {

            itBack.setVisibility(isShow ? View.VISIBLE : View.GONE);
            itBack.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void click(View v) {

                    finish();
                }
            });
        }
    }

    protected void showTile(boolean isShow, String title) {
        if (tvTitle != null) {

            if (CommonUtils.isNotEmpty(title)) {
                tvTitle.setText(title);

            }
            tvTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    protected void showRight(boolean isShow, int rosouce, NoDoubleClickListener listener) {

        if (itRight != null) {

            itRight.setVisibility(isShow ? View.VISIBLE : View.GONE);
            if (isShow) {
                itRight.setText(rosouce == 0 ? R.string.camera : rosouce);
            }
            itRight.setOnClickListener(listener);
        }

    }

    protected void showDialog(String msg) {

        ToastUtils.showToast(msg);

    }


    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(AppCompatActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    /**
     * 获取参数Variable
     */
    protected abstract int getBindingVariable();

    /**
     * @return layoutid
     */
    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void handleMsg(String msg);


}
