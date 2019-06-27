<template>
  <q-page :padding="$q.platform.is.desktop" class="justify-center">
    <!-- TABLE -->
    <q-pull-to-refresh :handler="refreshTableWithCallback" :disable="disabled">
      <div>
        <q-table
          :data="tableData"
          :columns="columns"
          :visible-columns="visibleColumns"
          row-key="id"
          :pagination.sync="tablePagination"
          :loading="tableLoading"
          @request="refreshTable"
          separator="cell">
          <div slot="top-left" slot-scope="props">
            <q-btn icon="create" color="secondary" :label="$t('label.new_request')" @click="openForm" :disable="disabled" />
            <q-btn v-if="admin" icon="build" color="secondary" class="q-ml-lg" :label="$t('label.executors_title')" @click="openExecutor" :disable="disabled" />
          </div>
          <q-tr slot="header" slot-scope="props" :props="props">
            <q-th v-for="col in props.cols" :key="col.name" :props="props">
              <span v-if="col.align === 'right'">{{ col.label }}</span>
              <q-icon name="filter_list"
                v-show="col.filter" />
              <span v-if="col.align !== 'right'">{{ col.label }}</span>
            </q-th>
          </q-tr>
          <template slot="top-right" slot-scope="props">
            <div class="row no-wrap items-start">
              <div class="col-auto">
                <q-btn
                  :label="$t('label.filters')"
                  icon-right="filter_list"
                  flat
                  no-caps
                  class="q-subheading"
                  @click="openFilter"
                  :disable="disabled"
                />
              </div>
              <div class="col-auto q-mt-sm">
                <q-table-columns
                  flat
                  v-model="visibleColumns"
                  :columns="columns"
                />
              </div>
              <div class="col-auto q-mt-xs">
                <q-btn
                  flat round dense
                  v-show="$q.platform.is.desktop"
                  :icon="props.inFullscreen ? 'fullscreen_exit' : 'fullscreen'"
                  @click="props.toggleFullscreen"
                />
              </div>
            </div>
          </template>
          <q-tr slot="body" slot-scope="props" :props="props" :class="(displayedRow && props.row.id == displayedRow.id) ? 'selected' : ''">
            <q-td v-for="col in props.cols" :key="col.name" :props="props">
              <q-btn v-if="'stageName' === col.name" flat round dense icon="list" @click="openStages(props.row)" class="on-left" />
              <q-btn v-if="'id' === col.name" @click="openCard(props.row)" :label="col.value"
                :icon-right="('id' === col.name && props.row.files && props.row.files.length) ? 'attach_file' : ' '"
                size="md" dense flat style="width:100%" align="right" color="secondary" text-color="primary" />
              <span v-else>{{ col.value }}</span>
            </q-td>
          </q-tr>
        </q-table>
      </div>
    </q-pull-to-refresh>

    <!-- FILTER FORM -->
    <q-modal v-model="filterOpened" @hide="refreshTable({})" :content-css="{minWidth: '40vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="closeFilter" />
          <q-toolbar-title>{{ $t('label.filters') }}</q-toolbar-title>
          <q-btn
            :label="$t('label.reset')"
            no-wrap
            color="secondary"
            align="right"
            @click="clearFilter" />
        </q-toolbar>

        <div style="max-width: 95vw;" class="q-pa-lg gutter-sm">
          <div class="q-pt-lg">
            <q-select v-model="mode" :options="modes" :float-label="$t('label.role')" clearable />
            <div v-for="(row, index) in columns" :key="index">
              <q-input v-if="'number' === row.type" type="number" v-model="row.filter" :float-label="row.label" clearable />
              <q-daterange v-else-if="'date' === row.type" v-model="row.filter" :label="row.label" format="YYYY-MM-DD" />
              <div v-else-if="'select' === row.type">
                <q-select v-if="row.options.length &gt; 0" v-model="row.filter" :options="row.options" :float-label="row.label" clearable />
              </div>
              <q-input v-else v-model="row.filter" :float-label="row.label" clearable />
            </div>
          </div>
        </div>
      </q-modal-layout>
    </q-modal>

    <!-- REQUEST CARD -->
    <q-modal :value="cardOpened" @hide="closeCard" :content-css="{minWidth: '40vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="cardOpened = false" />
          <q-toolbar-title>{{ $t('label.demand_title', {id: displayedRow.id, date: formatLocalDate(displayedRow.created.date, 'YYYY-MM-DD HH:mm')}) }}</q-toolbar-title>
          <q-btn-dropdown :label="$t('label.actions')" color="secondary" :disable="actionsDisabled">
            <q-list link>
              <q-item v-if="displayedRow.actions && displayedRow.actions.includes('DO_UPLOAD')" v-close-overlay @click.native="openUpload(displayedRow)">
                <q-item-side icon="backup" />
                <q-item-main>
                  <q-item-tile label>{{ $t('action.DO_UPLOAD') }}</q-item-tile>
                </q-item-main>
              </q-item>
              <q-item v-if="displayedRow.actions && displayedRow.actions.includes('DO_PRINT')" v-close-overlay @click.native="print(displayedRow)">
                <q-item-side icon="print" />
                <q-item-main>
                  <q-item-tile label>{{ $t('action.DO_PRINT') }}</q-item-tile>
                </q-item-main>
              </q-item>
              <q-item v-if="displayedRow.actions && displayedRow.actions.includes('DO_RATE')" v-close-overlay @click.native="openRate(displayedRow)">
                <q-item-side icon="star_border" />
                <q-item-main>
                  <q-item-tile label>{{ $t('action.DO_RATE') }}</q-item-tile>
                </q-item-main>
              </q-item>
              <q-item-separator inset />
              <q-item  v-for="(action, index) in displayedRow.actions" v-if="action.startsWith('STEP_')" :key="index" v-close-overlay @click.native="selectStage(action, displayedRow)">
                <q-item-side icon="launch" />
                <q-item-main>
                  <q-item-tile label>{{ $t('action.' + action) }}</q-item-tile>
                </q-item-main>
              </q-item>
            </q-list>
          </q-btn-dropdown>
        </q-toolbar>

        <div style="max-width: 95vw;">
          <q-uploader ref="uploader" color="secondary" multiple auto-expand :stack-label="$t('action.DO_UPLOAD')"
            url=""
            :url-factory="uploadUrl"
            :upload-factory="uploadFile"
            v-show="uploadOpened" />

          <q-btn-group rounded v-for="file in displayedRow.files" :key="file.id" v-if="displayedRow.actions.includes('DO_DOWNLOAD')" class="q-ma-sm">
            <q-btn rounded v-if="displayedRow.actions.includes('DO_UPLOAD')" @click="removeFile(file)" icon="delete_forever" color="primary" />
            <q-btn rounded :label="file.name" @click="downloadFile(file)" no-caps color="white" text-color="primary" >
              <q-chip floating color="primary">{{ formatSize(file.size) }}</q-chip>
            </q-btn>
          </q-btn-group>

          <div ref="card" class="q-pa-lg gutter-sm print">

            <div class="print-hidden" style="text-align: center;">
              <div><h1>{{ $t('label.demand_title2', {id: displayedRow.id}) }}</h1></div>
              <div><h2>{{ formatLocalDate(displayedRow.created.date, 'YYYY-MM-DD HH:mm') }}</h2></div>
            </div>

            <div><span class="q-title">{{ displayedRow.label }}</span></div>
            <q-field :label="$t('field.employee')" icon="person_outline" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs"><a :href="buildEmployeeLink(displayedRow.uid)" target="_blank">{{ displayedRow.name }}</a></div>
            </q-field>
            <q-field :label="$t('field.personnumber')" icon="assignment_ind" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.code }}</div>
            </q-field>
            <q-field :label="$t('field.position')" icon="work_outline" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.position }}</div>
            </q-field>
            <q-field :label="$t('field.organization')" icon="business" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.organizationName }} [{{ displayedRow.organizationId }}]</div>
            </q-field>
            <q-field :label="$t('field.department')" icon="people_outline" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.departmentName }} [{{ displayedRow.departmentId }}]</div>
            </q-field>
            <q-field :label="$t('field.manager')" icon="person" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs"><a :href="buildEmployeeLink(displayedRow.created.recipientUid)" target="_blank">{{ displayedRow.created.recipientName }}</a></div>
            </q-field>
            <q-field :label="$t('field.email')" icon="alternate_email" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs"><a :href="'mailto:' + displayedRow.email">{{ displayedRow.email }}</a></div>
            </q-field>
            <q-field :label="$t('field.phone')" icon="phone" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.phone }}</div>
            </q-field>
            <q-field :label="$t('field.location')" icon="place" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.location }}</div>
            </q-field>

            <div class="q-pt-lg">
              <q-field v-for="(row, index) in displayedRow.values" :key="index" icon="help_outline" icon-color="secondary" :label="row.label" :label-width="formLabelWidth" inset="full">
                <div class="col-auto q-pt-xs">{{ row.text }}</div>
              </q-field>
            </div>

            <q-field :label="$t('field.comment')" icon="insert_comment" icon-color="secondary" v-if="displayedRow.created.comment" :label-width="formLabelWidth" inset="full">
              <div class="col-auto q-pt-xs">{{ displayedRow.created.comment }}</div>
            </q-field>

            <q-field :label="$t('field.rating')" v-if="displayedRow.rate" icon="star_border" :label-width="formLabelWidth" inset="full">
              <q-rating v-model="displayedRow.rate" disable :max="5" color="orange" size="2rem" />
              <div class="col-auto q-pt-xs"><span class="print-hidden">{{ displayedRow.rate }}</span> {{ displayedRow.opinion }}</div>
            </q-field>

            <div class="print-hidden" style="text-align: center;">
              <hr />
              <table style="width:100%">
                <row v-for="(stage, index) in displayedRow.stages" :key="index">
                  <td style="width:30%">
                    <div style="white-space: nowrap">{{ $t('stage.' + stage.action) }}</div>
                    <div style="white-space: nowrap">{{ formatLocalDate(stage.date, 'YYYY-MM-DD HH:mm') }}</div>
                  </td>
                  <td style="width:65%">
                    <div>{{ stage.actorName }}</div>
                    <div style="font-style: italic">{{ stage.comment }}</div>
                  </td>
                </row>
              </table>
            </div>

          </div>
        </div>
      </q-modal-layout>
    </q-modal>

    <!-- RECIPIENT CHOOSE -->
    <q-modal :value="actionOpened" no-backdrop-dismiss no-esc-dismiss no-route-dismiss :content-css="{minWidth: '40vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="closeAction" />
          <q-toolbar-title>{{ $t('action.' + stage.action) }}</q-toolbar-title>
          <q-btn
            :label="$t('label.send')"
            no-wrap
            color="secondary"
            align="right"
            @click="saveStage" />
        </q-toolbar>
        <div style="max-width: 95vw;" class="q-pa-lg gutter-sm">
          <q-field :label="$t('field.recipient')" icon="person_outline" :label-width="formLabelWidth" inset="full">
            <q-search v-model="stage.recipientName" @clear="clearRecipient" @input="resetRecipient" no-icon :placeholder="$t('field.tip.type_name')" clearable @blur="$v.stage.recipientName.$touch" :error="$v.stage.recipientName.$error">
              <q-autocomplete @search="searchEmployee" @selected="selectedRecipient" :min-characters="formMinChars" :max-results="formMaxResult" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.request')" icon="insert_comment" :count="maxTextLength" :label-width="formLabelWidth" inset="full">
            <q-input v-model="stage.comment" type="textarea" :max-height="100" rows="4" :placeholder="$t('field.tip.request')" clearable />
          </q-field>
        </div>
      </q-modal-layout>
    </q-modal>

    <!-- RATING -->
    <q-modal :value="rateOpened" no-backdrop-dismiss no-esc-dismiss no-route-dismiss :content-css="{minWidth: '40vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="closeRate" />
          <q-toolbar-title>{{ $t('label.rating_title') }}</q-toolbar-title>
          <q-btn
            :label="$t('label.send')"
            no-wrap
            color="secondary"
            align="right"
            @click="saveRate" />
        </q-toolbar>
        <div style="max-width: 95vw;" class="q-pa-lg gutter-sm">
          <q-field :label="$t('field.rating')" icon="person_outline" :label-width="formLabelWidth" inset="full">
            <q-rating v-model="rate" :max="5" color="orange" size="2rem" />
          </q-field>
          <q-field label="Ваше мнение" icon="insert_comment" :count="maxTextLength" :label-width="formLabelWidth" inset="full">
            <q-input v-model="opinion" type="textarea" :max-height="100" rows="4" placeholder="Пояснительный текст" clearable />
          </q-field>
        </div>
      </q-modal-layout>
    </q-modal>

    <!-- STAGES LIST -->
    <q-modal :value="stagesOpened" @hide="closeStages" :content-css="{minWidth: '40vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="stagesOpened = false" />
          <q-toolbar-title>{{ $t('label.stages_title', {id: stagesRow.id}) }}</q-toolbar-title>
        </q-toolbar>
        <div v-for="(stage, index) in stagesRow.stages" :key="index" class="full-width q-ma-sm">
          <div class="row">
            <div class="col q-ma-sm">{{ $t('stage.' + stage.action) }}</div>
            <div class="col q-ma-sm">{{ formatLocalDate(stage.date, 'YYYY-MM-DD HH:mm') }}</div>
            <div class="col q-ma-sm"><a :href="buildEmployeeLink(stage.actorUid)" target="_blank">{{ stage.actorName }}</a></div>
          </div>
          <div class="row q-ma-sm text-italic">{{ stage.comment }}</div>
        </div>
      </q-modal-layout>
    </q-modal>

    <!-- REQUEST FORM -->
    <q-modal v-model="formOpened" no-backdrop-dismiss no-esc-dismiss no-route-dismiss :content-css="{minWidth: '60vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="closeForm" />
          <q-toolbar-title>{{ $t('label.demand_title3') }}</q-toolbar-title>
          <q-btn
            :label="$t('label.send')"
            no-wrap
            color="secondary"
            align="right"
            @click="saveForm" />
        </q-toolbar>

        <div style="max-width: 95vw;" class="q-pa-lg gutter-sm">
          <q-field :label="$t('field.employee')" icon="person_outline" :label-width="formLabelWidth" inset="full">
            <q-search v-model="employee.mask" @clear="clearEmployee" @input="resetEmployee" no-icon :placeholder="$t('field.tip.type_name')" clearable @blur="$v.employee.name.$touch" :error="$v.employee.name.$error">
              <q-autocomplete @search="searchEmployee" @selected="selectedEmployee" :min-characters="formMinChars" :max-results="formMaxResult" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.personnumber')" icon="assignment_ind" :label-width="formLabelWidth" inset="full">
            <q-input v-model="employee.code" :placeholder="$t('field.tip.choose_employee')" @blur="$v.employee.code.$touch" :error="$v.employee.code.$error" />
          </q-field>
          <q-field :label="$t('field.position')" icon="work_outline" :label-width="formLabelWidth" inset="full">
            <q-input v-model="employee.position" :placeholder="$t('field.tip.choose_employee')" />
          </q-field>
          <q-field :label="$t('field.organization')" icon="business" :label-width="formLabelWidth" inset="full">
            <q-search v-model="employee.organizationMask" @input="resetOrganization" no-icon @blur="$v.employee.organizationName.$touch" :error="$v.employee.organizationName.$error">
              <q-autocomplete @search="searchOrganization" @selected="selectedOrganization" :min-characters="formMinChars" :max-results="formMaxResult" :placeholder="$t('field.tip.choose_employee')" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.department')" icon="people_outline" :label-width="formLabelWidth" inset="full">
            <q-search v-model="employee.departmentMask" @input="resetDepartment" no-icon @blur="$v.employee.departmentName.$touch" :error="$v.employee.departmentName.$error">
              <q-autocomplete @search="searchDepartment" @selected="selectedDepartment" :min-characters="formMinChars" :max-results="formMaxResult" :placeholder="$t('field.tip.choose_employee')" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.manager')" icon="person" :label-width="formLabelWidth" inset="full">
            <q-search v-model="employee.chiefMask" @clear="clearChief" @input="resetChief" no-icon :placeholder="$t('field.tip.type_name')" clearable @blur="$v.employee.chiefName.$touch" :error="$v.employee.chiefName.$error">
              <q-autocomplete @search="searchChief" @selected="selectedChief" :min-characters="formMinChars" :max-results="formMaxResult" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.email')" icon="alternate_email" :label-width="formLabelWidth" inset="full">
            <q-input v-model="employee.email" :placeholder="$t('field.tip.choose_employee')" @blur="$v.employee.email.$touch" :error="$v.employee.email.$error" />
          </q-field>
          <q-field :label="$t('field.phone')" icon="phone" :label-width="formLabelWidth" inset="full">
            <q-input v-model="employee.phone" :placeholder="$t('field.tip.choose_employee')" />
          </q-field>
          <q-field :label="$t('field.location')" icon="place" :count="maxTextLength" :label-width="formLabelWidth" inset="full">
            <q-input v-model="employee.location" type="textarea" :max-height="100" rows="4" :placeholder="$t('field.tip.choose_location')" />
          </q-field>

          <q-field v-if="dynformList.length > 1" label="Тип заявки" icon="contact_support" icon-color="amber" dark :label-width="formLabelWidth" inset="full">
            <q-select :value="dynform.value" :options="dynformList" @change="selectDynform" :placeholder="$t('field.tip.choose_option')" inverted-light color="amber" separator />
          </q-field>
          <div class="q-pt-lg">
            <q-field v-for="(field, index) in dynform.fields" :key="index" icon="help_outline" icon-color="secondary" :label="field.label" :label-width="formLabelWidth" inset="full">
              <q-datetime v-if="field.type === 'date'" type="date" v-model="field.value" format="YYYY-MM-DD" format-model="string" />
              <q-datetime v-else-if="field.type === 'datetime'" type="datetime" v-model="field.value" format="YYYY-MM-DD HH:mm" format-model="string" />
              <q-input v-else-if="field.type === 'number'" type="number" v-model="field.value" />
              <q-select v-else-if="field.type === 'select'" v-model="field.value" :options="field.options" :placeholder="$t('field.tip.choose_option')" />
              <q-input v-else v-model="field.value" />
            </q-field>
          </div>

          <q-field v-if="uploadEnabled" :label="$t('field.files')" icon="cloud_upload" icon-color="secondary" :label-width="formLabelWidth" inset="full">
            <q-uploader ref="primeUploader" multiple auto-expand hide-upload-button :stack-label="$t('action.DO_UPLOAD')"
              url=""
              :url-factory="uploadUrl"
              :upload-factory="uploadFile" />
          </q-field>

          <q-field :label="$t('field.comment')" :count="maxTextLength" icon="insert_comment" icon-color="secondary" :label-width="formLabelWidth" inset="full">
            <q-input v-model="comment" type="textarea" :max-height="100" rows="4" :placeholder="$t('field.tip.comment')" clearable />
          </q-field>
        </div>
      </q-modal-layout>
    </q-modal>

    <!-- EXECUTOR PANEL -->
    <q-modal :value="executorOpened" no-backdrop-dismiss no-esc-dismiss no-route-dismiss :content-css="{minWidth: '40vw', minHeight: '60vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <q-btn
            flat
            round
            icon="reply"
            @click="closeExecutor" />
          <q-toolbar-title>{{ $t('label.executors_title') }}</q-toolbar-title>
          <q-btn
            :label="$t('label.send')"
            no-wrap
            color="secondary"
            align="right"
            @click="saveExecutor" />
        </q-toolbar>
        <div style="max-width: 95vw;" class="q-pa-lg gutter-sm">
          <q-field :label="$t('field.executor')" icon="person_outline" :label-width="formLabelWidth" inset="full">
            <q-search v-model="executor.name" @clear="clearExecutor" @input="resetExecutor" no-icon :placeholder="$t('field.tip.type_name')" clearable @blur="$v.executor.name.$touch" :error="$v.executor.name.$error">
              <q-autocomplete @search="searchEmployee" @selected="selectedExecutor" :min-characters="formMinChars" :max-results="formMaxResult" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.serviced_organization')" icon="business" :label-width="formLabelWidth" inset="full">
            <q-search v-model="executor.servicedOrganizationMask" @input="resetServicedOrganization" no-icon @blur="$v.executor.servicedOrganizationName.$touch" :error="$v.executor.servicedOrganizationName.$error">
              <q-autocomplete @search="searchOrganization" @selected="selectedServicedOrganization" :min-characters="formMinChars" :max-results="formMaxResult" :placeholder="$t('field.tip.choose_employee')" />
            </q-search>
          </q-field>
          <q-field :label="$t('field.serviced_department')" icon="people_outline" :label-width="formLabelWidth" inset="full">
            <q-search v-model="executor.servicedDepartmentMask" @input="resetServicedDepartment" no-icon>
              <q-autocomplete @search="searchServicedDepartment" @selected="selectedServicedDepartment" :min-characters="formMinChars" :max-results="formMaxResult" :placeholder="$t('field.tip.choose_employee')" />
            </q-search>
          </q-field>
        </div>
        <div v-for="(executor, index) in executors" :key="index" class="full-width q-ma-sm">
          <div class="row q-ma-sm">
            <div class="col-1 q-ma-sm">
              <q-btn
                round
                color="warning"
                icon="delete_forever"
                @click="removeExecutor(executor)" />
            </div>
            <div class="col-4 q-ma-sm q-body-1">{{ executor.name }}</div>
            <div class="col q-ma-sm text-weight-bold">
              <span>{{ executor.servicedOrganizationName }}</span>
              <br />
              <span class="q-caption">{{ executor.servicedDepartmentName }}</span>
            </div>
          </div>
        </div>

      </q-modal-layout>
    </q-modal>

  </q-page>
</template>

<style>
.selected {
  background-color: darkgray
}
.print-hidden {display: none;}
</style>

<script>
import 'whatwg-fetch'
import { required, email, numeric, maxLength } from 'vuelidate/lib/validators'
import QDaterange from '../components/QDaterange'
import { date } from 'quasar'
const { formatDate } = date
import Printd from 'printd'
import texts from '../data/texts.json'

export default {
  name: 'PageIndex',
  data () {
    return {
      admin: true,
      disabled: true,
      profile: '',
      config: {
        user: ''
      },
      texts: {},
      formLabelWidth: 4,
      formMinChars: 3,
      formMaxResult: 8,
      maxTextLength: 250,
      tableLoading: false,
      tablePagination: {
        page: 1,
        rowsNumber: 0
      },
      columns: [
        {
          name: 'id',
          required: true,
          label: this.$t('column.number'),
          align: 'right',
          field: 'id',
          sortable: true,
          filter: null,
          type: 'number'
        },
        {
          name: 'formName',
          label: this.$t('column.form'),
          field: row => this.dynformList.find(form => form.value === row.formName).label,
          align: 'left',
          sortable: false,
          filter: null,
          type: 'select',
          options: []
        },
        {
          name: 'stageName',
          label: this.$t('column.stage'),
          align: 'left',
          field: row => this.$t('stage.' + row.changed.action),
          sortable: false,
          filter: null,
          type: 'select',
          options: []
        },
        {
          name: 'name',
          label: this.$t('column.employee'),
          align: 'left',
          field: 'name',
          sortable: true,
          filter: ''
        },
        {
          name: 'createdAt',
          label: this.$t('column.created_at'),
          align: 'left',
          field: row => row.created.date,
          format: val => this.formatLocalDate(val, 'YYYY-MM-DD HH:mm'),
          sortable: false,
          filter: null,
          type: 'date'
        },
        {
          name: 'createdBy',
          label: this.$t('column.created_by'),
          align: 'left',
          field: row => row.created.actorName,
          sortable: false,
          filter: ''
        },
        {
          name: 'changedAt',
          label: this.$t('column.changed_at'),
          align: 'left',
          field: row => row.changed.date,
          format: val => this.formatLocalDate(val, 'YYYY-MM-DD HH:mm'),
          sortable: false,
          filter: null,
          type: 'date'
        },
        {
          name: 'changedBy',
          label: this.$t('column.changed_by'),
          align: 'left',
          field: row => row.changed.actorName,
          sortable: false,
          filter: ''
        }
      ],
      visibleColumns: ['id', 'name', 'createdAt', 'stageName'],
      tableData: [
      ],
      displayedRow: this.newDisplayedRow(),
      cardOpened: false,
      filterOpened: false,
      formOpened: false,
      employee: this.newEmployee(),
      dynformList: [],
      dynform: { fields: [] },
      uploadEnabled: false,
      // dynformValues: [],
      comment: '',
      opinion: '',
      rate: null,
      actionOpened: false,
      stagesRow: {},
      stagesOpened: false,
      stage: this.newStage(),
      executorOpened: false,
      executor: this.newExecutor(),
      uploadOpened: false,
      rateOpened: false,
      mode: null,
      modes: [
        {
          label: this.$t('mode.author'),
          value: 'AUTHOR'
        },
        {
          label: this.$t('mode.approver'),
          value: 'APPROVER'
        },
        {
          label: this.$t('mode.executor'),
          value: 'EXECUTOR'
        }
      ],
      executors: []
    }
  },
  computed: {
    actionsDisabled () {
      return !this.cardOpened ||
        typeof this.displayedRow.actions === 'undefined' ||
        this.displayedRow.actions.length === 0
    }
  },
  validations: {
    comment: { maxLength: maxLength(250) },
    opinion: { maxLength: maxLength(250) },
    employee: {
      name: { required },
      code: { required, numeric },
      email: { required, email },
      chiefName: { required },
      chiefUid: { required },
      organizationName: { required },
      organizationId: { required },
      departmentName: { required },
      departmentId: { required }
    },
    stage: {
      recipientName: { required },
      recipientUid: { required },
      comment: { required, maxLength: maxLength(250) }
    },
    executor: {
      name: { required },
      servicedOrganizationName: { required },
      servicedOrganizationId: { required }
    }
  },
  methods: {
    dictionaryError (error) {
      let message = this.formatErrorMessage(error)
      this.$q.notify({ message: `${this.$t('dictionary_error')}: ${message}` })
    },

    buildEmployeeLink (uid) {
      return `${this.config.serverDirectory}/employees/${encodeURIComponent(uid)}`
    },

    openStages (item) {
      this.stagesRow = item
      this.stagesOpened = true
    },

    closeStages () {
      this.stagesOpened = false
      this.stagesRow = {}
    },

    openCard (item) {
      if (!item.label) {
        const form = this.dynformList.find(form => form.value === item.formName)
        if (form) {
          item.label = form.label
          item.values.forEach(value => {
            const field = form.fields.find(field => value.name === field.name)
            if (field) {
              value.label = field.label
              if (field.type === 'select' && field.options) {
                const option = field.options.find(option => option.value === value.text)
                if (option) {
                  value.text = option.label
                } else {
                  console.log(`Undefined option ${value.text}`)
                }
              } else if (field.type === 'date') {
                value.text = this.formatLocalDate(value.text, 'YYYY-MM-DD')
              } else if (field.type === 'datetime') {
                value.text = this.formatLocalDate(value.text, 'YYYY-MM-DD HH:mm')
              }
            } else {
              console.log(`Undefined field ${value.name}`)
            }
          })
        } else {
          item.label = this.$t('unknown_type')
          console.log(`Undefined form ${item.formName}`)
        }
      }

      this.displayedRow = item
      this.cardOpened = true
    },

    closeCard () {
      this.cardOpened = false
      // this.displayedRow = this.newDisplayedRow()
      this.rateOpened = false
      this.uploadOpened = false
      this.$refs.uploader.reset()
    },

    newDisplayedRow () {
      return {
        created: {},
        changed: {},
        actions: [],
        values: [],
        files: []
      }
    },

    refreshTableWithCallback (done) {
      this.refreshTable({}, done)
    },

    refreshTable ({ pagination }, done) {
      this.tableLoading = true

      if (!pagination) {
        pagination = {
          page: 1,
          rowsPerPage: this.tablePagination.rowsPerPage,
          sortBy: 'id',
          descending: true
        }
      }

      let filter = {}
      for (let col of this.columns) {
        if (col.filter) { filter[col.name] = col.filter }
      }

      if (this.mode) {
        filter.mode = this.mode
      }

      filter.pagination = pagination

      const service = this.config.serverBack + '/demand/api/demands/'
      let params = {
        filter: JSON.stringify(filter),
        profile: this.profile
      }
      this.$http.get(service,
        {
          params: params
        })
        .then(response => {
          this.tableData = response.data.result
          this.tablePagination = response.data.pagination

          this.tableLoading = false
          this.$q.notify({ message: this.$t('refreshed'), color: 'tertiary', icon: 'thumb_up' })
          if (done !== undefined) done()
        })
        .catch(error => {
          let message = this.formatErrorMessage(error)
          this.$q.notify({ message: `${this.$t('loading_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
          this.tableLoading = false
        })
    },

    openFilter () {
      this.filterOpened = true
    },

    closeFilter () {
      this.filterOpened = false
    },

    clearFilter () {
      for (let index in this.columns) {
        let col = this.columns[index]
        col.filter = (!col.type || col.type === 'text') ? '' : null
      }
      this.mode = null
    },

    openForm () {
      if (!this.employee.uid) {
        this.findEmployeeByUid(this.config.user, this.selectedEmployee)
      }
      this.formOpened = true
    },

    clearDynform () {
      if (this.dynform.value) {
        this.dynform.fields.forEach(field => { if (field.value) field.value = null })
      }
    },

    selectDynform (value) {
      this.clearDynform()
      this.dynform = this.dynformList.find(form => form.value === value)
    },

    deselectDynform () {
      this.clearDynform()
      this.dynform = (this.dynformList.length === 1)
        ? this.dynformList[0]
        : { fields: [] }
    },

    closeForm () {
      this.$q.dialog({
        title: this.$t('confirm'),
        message: this.$t('confirm_form_cancel'),
        ok: this.$t('label.yes'),
        cancel: this.$t('label.no')
      }).then(() => {
        this.formOpened = false
        this.deselectDynform()
        this.$q.notify({ message: this.$t('form_cancel'), color: 'orange', icon: 'warning' })
      }).catch(() => {
        this.$q.notify({ message: this.$t('continue'), color: 'tertiary', icon: 'thumb_up' })
      })
    },

    saveForm () {
      this.$v.employee.$touch()
      this.$v.comment.$touch()

      if (this.$v.employee.$error || this.$v.comment.$error) {
        let errorMessage = []
        if (!this.$v.employee.code.required) errorMessage.push(this.$t('employee_code_required'))
        if (!this.$v.employee.code.numeric) errorMessage.push(this.$t('employee_code_invalid'))
        if (!this.$v.employee.email.required) errorMessage.push(this.$t('employee_email_required'))
        if (!this.$v.employee.email.email) errorMessage.push(this.$t('employee_email_invalid'))
        if (!this.$v.employee.chiefName.required) errorMessage.push(this.$t('chief_required'))
        else if (!this.$v.employee.chiefUid.required) errorMessage.push(this.$t('chief_invalid'))
        if (!this.$v.employee.organizationId.required) errorMessage.push(this.$t('organization_required'))
        if (!this.$v.employee.departmentId.required) errorMessage.push(this.$t('department_required'))
        if (!this.$v.comment.maxLength) errorMessage.push(this.$t('comment_length', {max: this.maxTextLength}))
        this.$q.notify({ message: `${this.$t('form_invalid')}: ${errorMessage.join(', ')}`, color: 'negative', icon: 'report_problem' })
        return
      }

      this.tableLoading = true

      let stage = {
        actorName: this.employee.name,
        actorUid: this.employee.uid,
        actorPosition: this.employee.position,
        recipientUid: this.employee.chiefUid,
        recipientName: this.employee.chiefName,
        recipientPosition: this.employee.chiefPosition,
        comment: this.comment
      }

      let request = {
        stages: [stage],
        values: []
      }

      for (let field in this.employee) {
        if (!field.startsWith('chief')) {
          request[field] = this.employee[field]
        }
      }

      if (this.dynform.value) {
        request.formName = this.dynform.value
        if (this.dynform.fields.length) {
          this.dynform.fields.forEach(field => {
            if (field.value) {
              let value = {
                name: field.name,
                text: field.value
              }
              request.values.push(value)
            }
          })

          if (request.values.length === 0) {
            this.tableLoading = false
            this.$q.notify({ message: this.$t('form_empty'), color: 'negative', icon: 'report_problem' })
            return
          }
        }
      } else {
        this.$q.notify({ message: this.$t('form_undefined'), color: 'negative', icon: 'report_problem' })
        return
      }

      request.profile = this.profile

      const service = this.config.serverBack + '/demand/api/demands/'
      this.$http.post(service, request)
        .then(response => {
          this.displayedRow = response.data

          if (this.uploadEnabled) {
            this.$refs.primeUploader.upload()
            this.$refs.primeUploader.reset()
          }

          this.formOpened = false
          this.comment = null
          this.deselectDynform()

          this.tableData.unshift(this.displayedRow)
          if (this.tableData.length > this.tablePagination.rowsPerPage) {
            this.tableData.splice(this.tablePagination.rowsPerPage, 1)
          }
          ++this.tablePagination.rowsNumber

          this.$q.notify({ message: this.$t('form_saved'), color: 'positive', icon: 'thumb_up' })
          this.tableLoading = false
        })
        .catch(error => {
          let message = this.formatErrorMessage(error)
          this.$q.notify({ message: `${this.$t('form_saving_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
          this.tableLoading = false
        })
    },

    searchOrganization (query, done) {
      return this.searchStructure(query, null, done)
    },

    resetOrganization (query) {
      if (this.employee) {
        if (query !== this.employee.organizationName) {
          this.employee.organizationName = null
          this.employee.organizationMask = query
          this.employee.organizationId = null
          this.employee.branch = null
          this.employee.departmentId = null
          this.employee.departmentName = null
          this.employee.departmentMask = null
          this.employee.chiefId = null
          this.employee.chiefUid = null
          this.employee.chiefName = null
          this.employee.chiefPosition = null
        }
      }
    },

    selectedOrganization (item) {
      console.log(`selectedOrganization ${item.label}`)
      this.employee.organizationName = item.object.name
      this.employee.organizationMask = this.employee.organizationName
      this.employee.organizationId = item.object.id
      this.employee.branch = item.object.branch
      this.employee.departmentId = null
      this.employee.departmentName = null
      this.employee.departmentMask = null
      this.employee.chiefId = null
      this.employee.chiefUid = null
      this.employee.chiefName = null
      this.employee.chiefPosition = null
    },

    parseStructures (departments) {
      return departments.map(dep => {
        let structure = JSON.parse(dep.json)
        structure.branch = dep.branch
        return {
          label: dep.name,
          value: structure.name,
          object: structure
        }
      })
    },

    resetDepartment (query) {
      if (this.employee) {
        if (query !== this.employee.departmentName) {
          this.employee.departmentId = null
          this.employee.departmentName = null
          this.employee.departmentMask = query
          this.employee.chiefId = null
          this.employee.chiefUid = null
          this.employee.chiefName = null
          this.employee.chiefPosition = null
        }
      }
    },

    searchDepartment (query, done) {
      if (this.employee.organizationId) {
        return this.searchStructure(query, this.employee.organizationId, done)
      } else {
        this.$q.notify('Сначала укажите организацию')
      }
    },

    searchStructure (query, ancestorId, done) {
      query = query.toLowerCase()
      query = query.replace(/ё/g, 'е')
      query = query.replace(/[^абвгдежзийклмнопрстуфхцчшщьыъэюя0-9]+/g, ' ')
      query = query.replace(/\s{2,}/, ' ')
      const endpoint = ancestorId ? 'findByNameContainingAndAncestor' : 'findByNameContainingAndParentIdIsNull'
      const api = '/directory/api/departments/search/'
      let service = this.config.serverDirectory + api + endpoint
      let params = {
        limit: this.formMaxResult,
        sort: 'name',
        name: query
      }
      if (ancestorId) {
        params.ancestorId = ancestorId
      }

      this.$http.get(service, { params: params })
        .then(response => done(this.parseStructures(response.data._embedded.departments)))
        .catch(err => this.dictionaryError(err))
    },

    findOrganizationByDepartmentId (departmentId, done) {
      const endpoint = 'findByChildAndParentIdIsNull'
      const api = '/directory/api/departments/search/'
      let service = this.config.serverDirectory + api + endpoint
      let params = {
        departmentId: departmentId
      }

      this.$http.get(service, { params: params })
        .then(response => done(response.data))
        .catch(err => this.dictionaryError(err))
    },

    findDepartmentById (departmentId, done) {
      const endpoint = departmentId
      const api = '/directory/api/departments/'
      let service = this.config.serverDirectory + api + endpoint

      this.$http.get(service)
        .then(response => done(response.data))
        .catch(err => this.dictionaryError(err))
    },

    selectedDepartment (item) {
      console.log(`selectedDepartment ${item.label}`)
      this.employee.departmentId = item.object.id
      this.employee.departmentName = item.object.name
      this.employee.departmentMask = this.employee.departmentName
      this.employee.branch = item.object.branch

      if (item.object.manager) {
        let manager = item.object.manager
        this.employee.chiefId = manager.id
        this.employee.chiefUid = manager.userId
        this.employee.chiefName = [manager.surname, manager.name, manager.patronymic].join(' ')
        this.employee.chiefPosition = manager.position.name
      }
    },

    newEmployee () {
      return {
        mask: '',
        name: '',
        uid: '',
        email: '',
        phone: '',
        code: '',
        position: '',
        location: '',
        staffId: '',
        chiefId: '',
        chiefUid: '',
        chiefName: '',
        chiefMask: '',
        chiefPosition: '',
        organizationId: '',
        organizationName: '',
        organizationMask: '',
        departmentId: '',
        departmentName: '',
        departmentMask: '',
        branch: ''
      }
    },

    parseEmployees (employees) {
      return employees.map(employee => {
        let fullLabel = [employee.lastName, employee.firstName, employee.middleName].join(' ')
        let staff = JSON.parse(employee.json)
        let fullName = [staff.surname, staff.name, staff.patronymic].join(' ')
        return {
          label: fullLabel,
          sublabel: staff.position.name,
          stamp: `${staff.id}`,
          value: fullName,
          object: staff
        }
      })
    },

    findEmployeeByUid (uid, done) {
      const api = '/directory/api/employees/search/'
      let service = this.config.serverDirectory + api + 'findFirstByUid'
      let params = {
        uid: uid
      }
      this.$http.get(service, { params: params })
        .then(response => done(this.parseEmployees([response.data])[0]))
        .catch(
          error => {
            this.dictionaryError(
              (error.response && error.response.status === 404)
                ? {
                  name: this.$t('employee_not_found'),
                  message: uid
                }
                : error)
          })
    },

    searchEmployee (query, done) {
      console.log(`searchEmployee: ${query}`)
      let parts = query.toLowerCase().split(' ')
      const endpoints = [
        '',
        'findByLastNameStartingWith',
        'findByLastNameAndFirstNameStartingWith',
        'findByLastNameAndFirstNameAndMiddleNameStartingWith'
      ]
      const sorting = [
        '',
        'lastName',
        'firstName',
        'middleName'
      ]
      const api = '/directory/api/employees/search/'
      let service = this.config.serverDirectory + api + endpoints[parts.length]
      let params = {
        limit: this.formMaxResult,
        sort: sorting[parts.length]
      }
      switch (parts.length) {
        case 3:
          params['middleName'] = parts[2]
          // break omitted
        case 2:
          params['firstName'] = parts[1]
          // break omitted
        case 1:
          params['lastName'] = parts[0]
          // break omitted
      }
      this.$http.get(service, { params: params })
        .then(response => done(this.parseEmployees(response.data._embedded.employees)))
        .catch(error => this.dictionaryError(error))
    },

    updateEmployee (staff) {
      this.clearEmployee()

      this.employee.uid = staff.userId
      this.employee.staffId = staff.id
      this.employee.name = [staff.surname, staff.name, staff.patronymic].join(' ')
      this.employee.code = staff.cypher
      this.employee.email = staff.email
      this.employee.position = staff.position.name

      this.employee.mask = this.employee.name

      this.findOrganizationByDepartmentId(
        (staff.orgIdFunc ? staff.orgIdFunc : staff.orgId),
        department => {
          let structure = JSON.parse(department.json)
          this.employee.organizationId = structure.id
          this.employee.organizationName = structure.name
          this.employee.organizationMask = this.employee.organizationName
        })

      this.findDepartmentById(
        (staff.orgIdFunc ? staff.orgIdFunc : staff.orgId),
        department => {
          let structure = JSON.parse(department.json)
          this.employee.departmentId = structure.id
          this.employee.departmentName = structure.name
          this.employee.departmentMask = this.employee.departmentName
          this.employee.branch = department.branch

          if (structure.manager) {
            if (staff.id === structure.manager.id) {
              this.findDepartmentById(structure.parentId,
                department => {
                  let structure = JSON.parse(department.json)
                  if (structure.manager) {
                    this.employee.chiefId = structure.manager.id
                    this.employee.chiefUid = structure.manager.userId
                    this.employee.chiefName = [structure.manager.surname, structure.manager.name, structure.manager.patronymic].join(' ')
                    this.employee.chiefPosition = structure.manager.position.name
                  }
                }
              )
            } else {
              this.employee.chiefId = structure.manager.id
              this.employee.chiefUid = structure.manager.userId
              this.employee.chiefName = [structure.manager.surname, structure.manager.name, structure.manager.patronymic].join(' ')
              this.employee.chiefPosition = structure.manager.position.name
            }

            this.employee.chiefMask = this.employee.chiefName
          }
        })

      this.employee.phone = staff.phoneList ? staff.phoneList.map(entry => entry.name).join('; ') : ''
      let phoneInt = staff.locationList ? staff.locationList.map(entry => entry.phoneInt).join('; ') : ''
      if (phoneInt) this.employee.phone += ' внутр. ' + phoneInt

      const addressRegex = /Местонахождения:\s*(.+?);\s/

      this.employee.location = staff.locationList ? staff.locationList.map(entry => {
        let address = entry.location.address
        let matches = addressRegex.exec(address)
        if (matches) address = matches[1]
        if (entry.office) {
          address += '\n'
          address += 'Помещение: ' + entry.office
        }
        return address
      }
      ).join('; ') : ''
    },

    selectedEmployee (item) {
      console.log(`selectedEmployee: ${item.label}`)
      this.updateEmployee(item.object)
    },

    resetEmployee (query) {
      if (this.employee) {
        if (query !== this.employee.name) {
          this.clearEmployee()
          this.employee.mask = query
        }
      }
    },

    clearEmployee () {
      this.employee = this.newEmployee()
    },

    searchChief (query, done) {
      if (this.employee.organizationId) {
        let parts = query.toLowerCase().split(' ')
        const endpoints = [
          '',
          'findByLastNameStartingWithAndDepartmentNested',
          'findByLastNameAndFirstNameStartingWithAndDepartmentNested',
          'findByLastNameAndFirstNameAndMiddleNameStartingWithAndDepartmentNested'
        ]
        const sorting = [
          '',
          'lastName',
          'firstName',
          'middleName'
        ]
        const api = '/directory/api/employees/search/'
        let service = this.config.serverDirectory + api + endpoints[parts.length]
        let params = {
          departmentId: this.employee.organizationId,
          limit: this.formMaxResult,
          sort: sorting[parts.length]
        }
        switch (parts.length) {
          case 3:
            params['middleName'] = parts[2]
          case 2:
            params['firstName'] = parts[1]
          case 1:
            params['lastName'] = parts[0]
        }
        this.$http.get(service, { params: params })
          .then((response) => {
            done(this.parseEmployees(response.data._embedded.employees))
          })
          .catch(error => {
            this.dictionaryError(error)
          })
      } else {
        this.$q.notify(this.$t('choose_organization'))
      }
    },

    selectedChief (item) {
      console.log(`selectedChief ${item.label}`)
      this.employee.chiefName = [item.object.surname, item.object.name, item.object.patronymic].join(' ')
      this.employee.chiefMask = this.employee.chiefName
      this.employee.chiefId = item.object.id
      this.employee.chiefUid = item.object.userId
      this.employee.chiefPosition = item.object.position.name
    },

    resetChief (query) {
      if (this.employee.chiefName) {
        if (query !== this.employee.chiefName) {
          this.clearChief()
          this.employee.chiefMask = query
        }
      }
    },

    clearChief () {
      this.employee.chiefId = null
      this.employee.chiefUid = null
      this.employee.chiefName = null
      this.employee.chiefPosition = null
    },

    newStage () {
      return {
        demand: { id: '' },
        action: '',
        actorUid: '',
        actorName: '',
        recipientUid: '',
        recipientName: '',
        recipientPosition: '',
        comment: ''
      }
    },

    selectStage (action, demand) {
      this.stage.demand.id = demand.id
      this.stage.action = action
      if (!action.startsWith('STEP_ASK')) {
        this.$q.dialog({
          title: this.$t('action.' + action),
          message: this.$t('add_message'),
          prompt: {
            model: ''
          },
          cancel: true,
          color: 'secondary'
        }).then(text => {
          this.stage.comment = text
          this.saveStage()
        }).catch(() => {
          this.$q.notify({ message: this.$t('cancelled'), color: 'orange', icon: 'warning' })
        })
      } else {
        if (action === 'STEP_ASKREFUSE') {
          this.stage.recipientUid = demand.changed.actorUid
          this.stage.recipientName = demand.changed.actorName
          this.stage.recipientPosition = demand.changed.actorPosition
        }
        this.actionOpened = true
      }
    },

    saveStage () {
      if (this.actionOpened) {
        this.$v.stage.$touch()

        if (this.$v.stage.$error) {
          let errorMessage = []
          if (!this.$v.stage.recipientName.required) errorMessage.push(this.$t('recipient_required'))
          else if (!this.$v.stage.recipientUid.required) errorMessage.push(this.$t('recipient_invalid'))
          if (!this.$v.stage.comment.required) errorMessage.push(this.$t('add_message'))
          if (!this.$v.stage.comment.maxLength) errorMessage.push(this.$t('textarea_length'))
          this.$q.notify({ message: `${this.$t('form_invalid')}: ${errorMessage.join(', ')}`, color: 'negative', icon: 'report_problem' })
          return
        }
      }

      this.tableLoading = true
      const service = `${this.config.serverBack}/demand/api/demands/${this.stage.demand.id}/stages`
      this.$http.post(service, this.stage)
        .then(response => {
          this.actionOpened = false
          this.stage = this.newStage()

          this.refreshTable(this.tablePagination)
          this.closeCard()
          this.$q.notify({ message: this.$t('stage_saved'), color: 'positive', icon: 'thumb_up' })

          this.tableLoading = false
        })
        .catch(error => {
          let message = this.formatErrorMessage(error)
          this.$q.notify({ message: `${this.$t('stage_saving_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
          this.tableLoading = false
        })
    },

    selectedRecipient (item) {
      console.log(`selectedActor: ${item.label}`)
      let staff = item.object
      this.stage.recipientUid = staff.userId
      this.stage.recipientName = [staff.surname, staff.name, staff.patronymic].join(' ')
      this.stage.recipientPosition = staff.position.name
    },

    resetRecipient (query) {
      if (this.stage && this.stage.recipientName) {
        if (query !== this.stage.recipientName) {
          this.clearRecipient()
        }
      }
    },

    clearRecipient () {
      this.stage.recipientUid = null
      this.stage.recipientName = null
      this.stage.recipientPosition = null
    },

    closeAction () {
      this.actionOpened = false
    },

    print (demand) {
      this.printOpened = true
      const printer = new Printd()
      printer.print(
        this.$refs.card,
        `
        body {font-size: 12pt;}
        [aria-hidden="true"], .q-field-icon {display: none;}
        .print {display: table; margin: 0 40pt; font-family: "Courier New", Courier, monospace;}
        .print-hidden {display: inline;}
        h1 {font-size: 16pt;}
        h2 {font-size: 14pt;}
        div .row .col {display: table-row;}
        div .row .col>div {display: table-cell; padding: 2pt; font-size: 12pt;}
        div.q-field-label {width: 130pt;}
        div.q-field-content {width: 340pt; font-weight: bold;}
        div.q-pt-lg {padding: 15pt}
        a {text-decoration: none; color: #000;}
        span.q-title {font-size: 14pt; font-weight: bold;}
        table, table tr: {padding: 0; margin: 0;}
        table td: {padding: 2pt 1pt 1pt 0; margin: 0;}
        `)
    },

    openRate (demand) {
      this.rateOpened = true
    },

    saveRate () {
      if (this.displayedRow) {
        this.$v.opinion.$touch()
        if (!this.$v.opinion.maxLength) {
          this.$q.notify({ message: this.$t('textarea_length', {max: this.maxTextLength}), color: 'negative', icon: 'report_problem' })
          return
        }

        this.displayedRow.rate = this.rate
        this.displayedRow.opinion = this.opinion

        const service = `${this.config.serverBack}/demand/api/demands/${this.displayedRow.id}`
        this.$http.put(service, this.displayedRow)
          .then(response => {
            this.$q.notify({ message: this.$t('rating_sent'), color: 'positive', icon: 'thumb_up' })
            this.displayedRow.actions = response.data.actions
            this.closeRate()
          })
          .catch(error => {
            let message = this.formatErrorMessage(error)
            this.$q.notify({ message: `${this.$t('saving_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
          })
      }
    },

    closeRate () {
      this.rateOpened = false
      this.rate = null
      this.opinion = null
    },

    openUpload (demand) {
      this.uploadOpened = true
    },

    async uploadUrl (file) {
      return `${this.config.serverBack}/demand/api/demands/${this.displayedRow.id}/files`
    },

    uploadFile (file, updateProgress) {
      const form = new FormData()
      form.append('file', file)
      return new Promise((resolve, reject) => {
        this.uploadUrl(file).then(url => {
          this.$http.post(
            url,
            form,
            {
              headers: {
                'Content-Type': 'multipart/form-data'
              },
              onUploadProgress: progressEvent => updateProgress(progressEvent.loaded)
            })
            .then(response => {
              if (this.displayedRow) {
                if (!this.displayedRow.files) {
                  this.displayedRow.files = []
                }
                this.displayedRow.files.push(response.data)
                this.$q.notify({ message: this.$t('file_sent', {name: file.name}), color: 'positive', icon: 'thumb_up' })
              }
              resolve(file)
            })
            .catch(error => {
              let message = this.formatErrorMessage(error)
              this.$q.notify({ message: `${this.$t('upload_error', {name: file.name})}: ${message}` })
              reject(error)
            })
        })
      })
    },

    downloadFile (file) {
      if (file && file.id) {
        const service = `${this.config.serverBack}/demand/api/demands/files/${file.id}`
        this.$http.get(service,
          {
            responseType: 'blob'
          })
          .then(response => {
            let blob = new Blob([response.data], { type: file.contentType || 'application/octet-stream' })
            if (typeof window.navigator.msSaveBlob !== 'undefined') { // IE
              window.navigator.msSaveBlob(blob, file.name)
            } else {
              let blobURL = window.URL.createObjectURL(blob)
              let tempLink = document.createElement('a')
              tempLink.style.display = 'none'
              tempLink.href = blobURL
              tempLink.setAttribute('download', file.name)
              if (typeof tempLink.download === 'undefined') { // !HTML5
                tempLink.setAttribute('target', '_blank')
              }
              document.body.appendChild(tempLink)
              tempLink.click()
              document.body.removeChild(tempLink)
              window.URL.revokeObjectURL(blobURL)
            }
          })
          .catch(error => {
            let message = this.formatErrorMessage(error)
            this.$q.notify({ message: `${this.$t('download_error', {name: file.name})}: ${message}`, color: 'negative', icon: 'report_problem' })
          })
      } else {
        this.$q.notify({ message: this.$t('unknown_file'), color: 'negative', icon: 'report_problem' })
      }
    },

    removeFile (file) {
      this.$q.dialog({
        title: this.$t('label.file_deletion'),
        message: this.$t('confirm_file_deletion'),
        ok: true,
        cancel: true
      })
        .then(() => {
          const service = `${this.config.serverBack}/demand/api/demands/files/${file.id}`
          this.$http.delete(service)
            .then(() => {
              this.$q.notify({ message: this.$t('file_deleted', {name: file.name}), color: 'positive', icon: 'thumb_up' })
              if (this.displayedRow) {
                for (let i = 0; i < this.displayedRow.files.length; i++) {
                  if (this.displayedRow.files[i].id === file.id) {
                    this.displayedRow.files.splice(i, 1)
                    break
                  }
                }
              }
            })
            .catch(error => {
              let message = this.formatErrorMessage(error)
              this.$q.notify({ message: `${this.$t('file_deletion_error', {name: file.name})}: ${message}` })
            })
        })
        .catch(() => {
          this.$q.notify({ message: this.$t('cancelled'), color: 'orange', icon: 'warning' })
        })
    },

    newExecutor () {
      return {
        profile: this.profile,
        mask: null,
        name: null,
        uid: null,
        email: null,
        phone: null,
        staffId: null,
        organizationId: null,
        organizationName: null,
        departmentId: null,
        departmentName: null,
        servicedOrganizationId: null,
        servicedOrganizationName: null,
        servicedOrganizationMask: null,
        servicedOrganizationBranch: null,
        servicedDepartmentId: null,
        servicedDepartmentName: null,
        servicedDepartmentMask: null,
        servicedDepartmentBranch: null,
        branch: null
      }
    },

    selectedExecutor (item) {
      console.log(`selectedExecutor: ${item.label}`)
      let staff = item.object
      this.executor.uid = staff.userId
      this.executor.staffId = staff.id
      this.executor.name = [staff.surname, staff.name, staff.patronymic].join(' ')
      this.executor.email = staff.email

      this.executor.mask = this.employee.name

      this.findOrganizationByDepartmentId(
        (staff.orgIdFunc ? staff.orgIdFunc : staff.orgId),
        department => {
          let structure = JSON.parse(department.json)
          this.executor.organizationId = structure.id
          this.executor.organizationName = structure.name
        })

      this.findDepartmentById(
        (staff.orgIdFunc ? staff.orgIdFunc : staff.orgId),
        department => {
          let structure = JSON.parse(department.json)
          this.executor.departmentId = structure.id
          this.executor.departmentName = structure.name
        })

      this.executor.phone = staff.phoneList ? staff.phoneList.map(entry => entry.name).join('; ') : ''
    },

    openExecutor () {
      this.loadExecutors()
      this.executorOpened = true
    },

    resetExecutor (query) {
      if (this.executor && this.executor.name) {
        if (query !== this.executor.name) {
          this.clearExecutor()
        }
      }
    },

    clearExecutor () {
      this.executor = this.newExecutor()
    },

    closeExecutor () {
      this.executorOpened = false
      this.clearExecutor()
      this.executors = []
    },

    resetServicedOrganization (query) {
      if (this.executor) {
        if (query !== this.employee.servicedOrganizationName) {
          this.executor.servicedOrganizationName = null
          this.executor.servicedOrganizationMask = null
          this.executor.servicedOrganizationId = null
          this.executor.servicedOrganizationBranch = null
          this.executor.servicedDepartmentId = null
          this.executor.servicedDepartmentName = null
          this.executor.servicedDepartmentMask = null
          this.executor.servicedDepartmentBranch = null
        }
      }
    },

    selectedServicedOrganization (item) {
      console.log(`selectedServicedOrganization ${item.label}`)
      this.executor.servicedOrganizationName = item.object.name
      this.executor.servicedOrganizationMask = this.executor.servicedOrganizationName
      this.executor.servicedOrganizationId = item.object.id
      this.executor.servicedOrganizationBranch = item.object.branch
      this.executor.servicedDepartmentId = null
      this.executor.servicedDepartmentName = null
      this.executor.servicedDepartmentMask = null
    },

    searchServicedDepartment (query, done) {
      if (this.executor.servicedOrganizationId) {
        return this.searchStructure(query, this.executor.servicedOrganizationId, done)
      } else {
        this.$q.notify('Сначала укажите организацию')
      }
    },

    resetServicedDepartment (query) {
      if (this.executor) {
        if (query !== this.executor.servicedDepartmentName) {
          this.executor.servicedDepartmentId = null
          this.executor.servicedDepartmentName = null
          this.executor.servicedDepartmentMask = query
          this.executor.servicedDepartmentBranch = null
        }
      }
    },

    selectedServicedDepartment (item) {
      console.log(`selectedServicedDepartment ${item.label}`)
      this.executor.servicedDepartmentId = item.object.id
      this.executor.servicedDepartmentName = item.object.name
      this.executor.servicedDepartmentMask = this.executor.servicedDepartmentName
      this.executor.servicedDepartmentBranch = item.object.branch
    },

    saveExecutor () {
      if (this.executorOpened) {
        this.$v.executor.$touch()
        if (this.$v.executor.$error) {
          this.$q.notify({ message: this.$t('form_invalid'), color: 'negative', icon: 'report_problem' })
          return
        }
      }

      this.executor.profile = this.profile
      this.executor.branch = (this.executor.servicedDepartmentBranch || this.executor.servicedOrganizationBranch)
      console.log(JSON.stringify(this.executor))

      const service = `${this.config.serverBack}/demand/api/executors`
      this.$http.post(service, this.executor)
        .then(response => {
          this.executor = this.newExecutor()
          this.$q.notify({ message: this.$t('executor_saved'), color: 'positive', icon: 'thumb_up' })
          this.loadExecutors()
        })
        .catch(error => {
          let message = this.formatErrorMessage(error)
          this.$q.notify({ message: `${this.$t('form_saving_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
        })
    },

    loadExecutors () {
      const service = `${this.config.serverBack}/demand/api/executors`
      let params = {
        profile: this.profile
      }
      this.$http.get(service, {params: params})
        .then(response => {
          this.executors = response.data
          this.$q.notify({ message: this.$t('refreshed'), color: 'tertiary', icon: 'thumb_up' })
        })
        .catch(error => {
          let message = this.formatErrorMessage(error)
          this.$q.notify({ message: `${this.$t('dictionary_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
        })
    },

    removeExecutor (executor) {
      this.$q.dialog({
        title: this.$t('label.executor_deletion'),
        message: this.$t('confirm_executor_deletion'),
        ok: true,
        cancel: true
      })
        .then(() => {
          const service = `${this.config.serverBack}/demand/api/executors/${executor.id}`
          this.$http.delete(service)
            .then(() => {
              this.$q.notify({ message: this.$t('executor_deleted', {name: executor.name}), color: 'positive', icon: 'thumb_up' })
              this.loadExecutors()
            })
            .catch(error => {
              let message = this.formatErrorMessage(error)
              this.$q.notify({ message: `${this.$t('executor_deletion_error', {name: executor.name})}: ${message}` })
            })
        })
        .catch(() => {
          this.$q.notify({ message: this.$t('cancelled'), color: 'orange', icon: 'warning' })
        })
    },

    formatLocalDate (zuluDate, pattern) {
      let d = new Date(zuluDate)
      // d.setHours(d.getHours() - (d.getTimezoneOffset() / 60))
      return formatDate(d, pattern)
    },

    formatSize (bytes) {
      var thresh = 1024
      if (Math.abs(bytes) < thresh) {
        return bytes + ' B'
      }
      const units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
      let u = -1
      do {
        bytes /= thresh
        ++u
      } while (Math.abs(bytes) >= thresh && u < units.length - 1)
      return bytes.toFixed(1) + ' ' + units[u]
    },

    formatErrorMessage (error) {
      if (error.response) {
        let cause = error.response.data
        let message = ''
        while (cause) {
          if (cause.message) {
            message = cause.message
          }
          cause = cause.cause
        }
        let status = this.texts['error-' + error.response.status] || error.response.statusText || error.response.status
        return (`${status} : ${message}`)
      }
      console.log(JSON.stringify(error))
      return JSON.stringify(this.$t('undefined_error'))
    },

    initProfile () {
      let url = window.location.href
      let indexOfQuery = url.indexOf('?')
      if (indexOfQuery > 0) {
        let query = url.substring(indexOfQuery + 1)
        indexOfQuery = query.indexOf('#')
        if (indexOfQuery > 0) {
          query = query.substring(0, indexOfQuery)
        }

        let pairs = query.split('&')
        for (let index in pairs) {
          let entry = pairs[index].split('=')
          if (entry[0] === 'profile') {
            this.profile = decodeURIComponent(entry[1])
            break
          }
        }
      }

      if (!this.profile) {
        this.profile = 'TEST'
      }

      console.log(`Profile: ${this.profile}`)
    },

    loadConfig () {
      this.config.serverBack = location.protocol + '//' + location.hostname + ':' + (location.hostname === 'localhost' ? '8181' : location.port)

      return new Promise((resolve, reject) => {
        const service = this.config.serverBack + '/demand/api/config'
        fetch(service)
          .then(response => {
            if (response.status === 200) {
              response.json()
                .then(data => {
                  this.config.serverAuth = data.auth
                  this.config.serverDirectory = data.directory
                  resolve()
                })
                .catch(error => reject(error))
            } else {
              reject(new Error(response.status + ':' + response.statusText))
            }
          })
          .catch(error => reject(error))
      }
      )
    },

    loadActions () {
      const service = this.config.serverBack + '/demand/api/rules/actions'
      let params = {
        profile: this.profile
      }
      return this.$http.get(service, {params: params})
    },

    loadForms () {
      const service = this.config.serverBack + '/demand/api/forms'
      let params = {
        profile: this.profile
      }
      return this.$http.get(service, {params: params})
    },

    identify () {
      const service = this.config.serverBack + '/demand/api/user/identify'
      let params = {
        profile: this.profile
      }
      return this.$http.get(service, { params: params })
    },

    authForm (data) {
      return this.$login({
        prompt: {
          model: {
            username: '',
            password: ''
          }
        },
        cancel: true,
        preventClose: true,
        color: 'secondary'
      })
    }
  },

  beforeCreate () {
    let lang = navigator.language.toLowerCase()
    import(`quasar-framework/i18n/${lang}`).then(lang => {
      this.$q.i18n.set(lang.default)
    }).catch(error => {
      let message = this.formatErrorMessage(error)
      console.log(`Error on loading resources of ${lang}: ${message}`)
      if (lang.length > 2) {
        lang = lang.substring(0, 2)
        import(`quasar-framework/i18n/${lang}`).then(lang => {
          this.$q.i18n.set(lang.default)
        }).catch(error => {
          let message = this.formatErrorMessage(error)
          console.log(`Error on loading resources of ${lang}: ${message}`)
        })
      }
    })
  },

  beforeMount () {
    this.initProfile()
    this.texts = texts
  },

  mounted () {
    this.loadConfig()
      .then(() => {
        this.$http.authClient.defaults.baseURL = `${this.config.serverAuth}/authservice/api/oauth/token`
        this.$http.authClient.defaults.params = {
          grant_type: 'client_credentials',
          client: this.profile
        }

        this.$http.authClient.defaults.login = this.authForm
        this.identify()
          .then(user => {
            this.config.user = user.data
            this.findEmployeeByUid(this.config.user, this.selectedEmployee)
            Promise.all([
              this.loadActions(),
              this.loadForms()
            ]).then(([actions, forms]) => {
              this.dynformList = forms.data
              this.deselectDynform()
              if (this.dynformList.length > 1) {
                let typeColumn = this.columns[1]
                typeColumn.options = this.dynformList
              }

              let stageColumn = this.columns.find(col => col.name === 'stageName')
              stageColumn.options = []
              for (let index in actions.data) {
                let key = actions.data[index]
                if (key.startsWith('STEP_')) {
                  let label = this.$t('stage.' + key, '')
                  if (label) {
                    stageColumn.options.push({
                      label: label,
                      value: key
                    })
                  }
                }
              }

              this.uploadEnabled = (actions.data.indexOf('DO_UPLOAD') >= 0)

              this.disabled = false
              this.refreshTable({})
            })
              .catch(error => {
                let message = this.formatErrorMessage(error)
                this.$q.notify({ message: `${this.$t('initialization_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
              })
          })
          .catch(error => {
            let message = this.formatErrorMessage(error)
            this.$q.notify({ message: `${this.$t('identification_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
          })
      })
      .catch(error => {
        let message = this.formatErrorMessage(error)
        this.$q.notify({ message: `${this.$t('configuration_error')}: ${message}`, color: 'negative', icon: 'report_problem' })
      })
  },

  components: {
    QDaterange
  }
}
</script>
