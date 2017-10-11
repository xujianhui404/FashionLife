package fashionlife.com.test;

import fashionlife.com.base.AbsBasePresenter;

/**
 * Created by lovexujh on 2017/9/19
 */

public class TestPresenter extends AbsBasePresenter<TestView> {

    private TestModel model;
    private TestView view;

    public TestPresenter(TestView view) {
        this.view = view;
        model = new TestModel();
    }

    public void requestData() {
        model.reqeustData("123", new TestModel.TestModelCallBack() {
            @Override
            public void onGetData() {
                if (view != null) {
                    view.onSuccess("success");
                }
            }

            @Override
            public void onFailed() {
                if (view != null) {
                    view.onFailed("failed");
                }
            }
        });
    }

}
