export default {
  apiUrl: 'https://localhost:8443/exampleapi/control',
  formApiUrl: 'https://localhost:8443/example/control',
  hostUrl: 'https://localhost:8443',
  listForDropDown: {path: '/listForDropDown', tokenRequired: false},
  main: {path: '/main', tokenRequired: false},
  editExampleLayer: {path: '/EditExampleLayer', tokenRequired: false},
  authentication: {path: '/authentification', tokenRequired: false},
  checkAuthToken: {path: '/checkAuthToken', tokenRequired: true},
  login: {path: '/login', tokenRequired: false},
  ping: {path: '/ping', tokenRequired: false},
  ajaxCheckLogin: {path: '/ajaxCheckLogin', tokenRequired: false},
  findExampleLayer: {path: '/FindExampleLayer', tokenRequired: false},
  listExampleLayer: {path: '/ListExampleLayer', tokenRequired: false},
  portalPageDetail: {path: '/portalPageDetail', tokenRequired: false},
  showPortlet: {path: '/showPortletFj', tokenRequired: false},

  components: {
    Form: 'vue-form',
    ListWrapper: 'vue-list-wrapper',
    Header: 'vue-header',
    HeaderRow: 'vue-header-row',
    HeaderRowCell: 'vue-header-row-cell',
    HeaderRowFormCell: 'vue-header-row-form-cell',
    ItemRow: 'vue-item-row',
    ItemRowCell: 'vue-item-row-cell',
    ItemRowFormCell: 'vue-item-row-form-cell',
    SingleWrapper: 'vue-single-wrapper',
    FieldRow: 'vue-field-row',
    FieldRowTitleCell: 'vue-field-row-title-cell',
    FieldRowWidgetCell: 'vue-field-row-widget-cell',
    FieldGroup: '?', // todo
    FieldTitle: 'vue-title',
    TextField: 'vue-text',
    TextareaField: 'vue-text-area',
    DisplayField: 'vue-title',
    DateTimeField: 'vue-date-time',
    TextFindField: 'vue-text-find',
    DropDownField: 'vue-drop-down',
    RadioField: 'vue-radio',
    HiddenField: 'vue-hidden',
    HyperlinkTitle: 'vue-hyperlink',
    HyperlinkField: 'vue-hyperlink',
    SubmitField: 'vue-submit'
  }
}
