package com.bondex.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.bondex.library.R;
import com.bondex.library.util.NoDoubleClickListener;
import com.bondex.library.util.ToastUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.bondex.library.app.PhotoApplication.getContext;

/**
 * date: 2020/7/22
 *
 * @author: ysl
 * description:
 */
public abstract class BaseFragment<V extends ViewDataBinding, M extends BaseViewMode> extends Fragment {


    protected V binding;
    protected M viewModel;
    protected AVLoadingIndicatorView loading;

    protected NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void click(View v) {
            myClick(v);
        }
    };

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = view.findViewById(R.id.avl_loading);
        initViewModel();
        performDataBinding();
        initView();
        initListener();
    }


    private void performDataBinding() {
        View view = binding.getRoot();

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
        viewModel = (M) createViewModel(getActivity(), modelClass);
        viewModel.attachUi();
        viewModel.setContext(getContext());
//生命周期监听
        getLifecycle().addObserver(viewModel);

    }

    private void initObserver() {

        viewModel.loading.observe(this, loadingObserver);
        viewModel.toastLiveData.observe(this, toastObserver);
        viewModel.msgLiveData.observe(this, msgObserver);
    }

    private void removeObserver() {

        if (viewModel.loading.hasObservers()) {
            viewModel.loading.removeObserver(loadingObserver);
        }
        if (viewModel.toastLiveData.hasObservers()) {
            viewModel.toastLiveData.removeObserver(toastObserver);
        }
        if (viewModel.msgLiveData.hasObservers()) {
            viewModel.msgLiveData.removeObserver(msgObserver);
        }
        getLifecycle().removeObserver(viewModel);

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
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
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

    protected abstract void myClick(View v);


    @Override
    public void onStart() {
        super.onStart();
        initObserver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeObserver();
        viewModel.detachUi();
    }

}
